package server.systems.combat;

import com.artemis.E;
import com.artemis.annotations.Wire;
import com.esotericsoftware.minlog.Log;
import component.console.ConsoleMessage;
import component.entity.character.states.Heading;
import component.entity.character.status.Health;
import component.entity.world.CombatMessage;
import component.physics.AttackAnimation;
import component.position.WorldPos;
import server.database.model.modifiers.Modifiers;
import server.systems.CharacterTrainingSystem;
import server.systems.entity.EffectEntitySystem;
import server.systems.entity.SoundEntitySystem;
import server.systems.manager.MapManager;
import server.systems.manager.ObjectManager;
import server.systems.manager.WorldManager;
import server.systems.network.MessageSystem;
import server.systems.network.UpdateTo;
import shared.interfaces.CharClass;
import shared.interfaces.FXs;
import shared.network.notifications.EntityUpdate;
import shared.objects.types.*;
import shared.util.EntityUpdateBuilder;
import shared.util.Messages;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.artemis.E.E;
import static server.utils.WorldUtils.WorldUtils;

@Wire(injectInherited = true)
public class PhysicalCombatSystem extends AbstractCombatSystem {

    private static final String MISS = "Fallas!";
    private static final float ASSASIN_STAB_FACTOR = 1.5f;
    private static final float NORMAL_STAB_FACTOR = 1.4f;
    private static final int TIME_TO_MOVE_1_TILE = 250;
    // Injected Systems
    private MapManager mapManager;
    private ObjectManager objectManager;
    private WorldManager worldManager;
    private CharacterTrainingSystem characterTrainingSystem;
    private MessageSystem messageSystem;
    private SoundEntitySystem soundEntitySystem;
    private EffectEntitySystem effectEntitySystem;

    @Override
    protected void failed(int entityId, Optional<Integer> targetId) {
        notify(targetId.isPresent() ? targetId.get() : entityId, CombatMessage.physic(MISS));
    }

    @Override
    public boolean canAttack(int entityId, Optional<Integer> target) {
        final E e = E(entityId);
        if (e != null && e.hasStamina() && e.getStamina().min < e.getStamina().max * STAMINA_REQUIRED_PERCENT / 100) {
            notifyCombat(entityId, Messages.NOT_ENOUGH_ENERGY);
            return false;
        }
        if (e != null && e.hasHealth() && e.getHealth().min == 0) {
            notifyCombat(entityId, Messages.DEAD_CANT_ATTACK);
            return false;
        }

        if (target.isPresent()) {
            int targetId = target.get();
            E t = E(targetId);
            if (t == null) {
                Log.info("Can't find target");
                return false;
            }
            if (!isValidTarget(entityId, targetId)) {

                return false;
            }

            if (t.hasHealth() && t.getHealth().min == 0) {
                // no podes atacar un muerto
                notifyCombat(entityId, Messages.CANT_ATTACK_DEAD);
                return false;
            }

            // es del otro team? ciuda - crimi
            if (!e.isCriminal() && !t.isCriminal() /* TODO agregar seguro */) {
                // notifyCombat(userId, CANT_ATTACK_CITIZEN);
                // TODO descomentar: return false;
            }

            // TODO attack power can be bow

            int evasionPower = evasionPower(targetId) + (E(targetId).hasShield() ? shieldEvasionPower(targetId) : 0);
            double prob = Math.max(10, Math.min(90, 50 + (weaponAttackPower(entityId) - evasionPower) * 0.4));
            if (ThreadLocalRandom.current().nextInt(101) <= prob) {
                return true;
            } else {
                int skills = 200;
                prob = Math.max(10, Math.min(90, 100 * 100 / skills));

                // shield evasion
                if (E(targetId).hasShield() && ThreadLocalRandom.current().nextInt(101) <= prob) {
                    notifyCombat(targetId, Messages.SHIELD_DEFENSE);
                    notifyCombat(entityId, Messages.DEFENDED_WITH_SHIELD, getName(targetId));
                    // TODO shield animation
                    soundEntitySystem.add(targetId, 37);
                } else {
                    notifyCombat(entityId, Messages.ATTACK_FAILED);
                    notifyCombat(targetId, Messages.ATTACKED_AND_FAILED, getName(entityId));
                }
            }
        }
        return false;
    }

    private boolean isValidTarget(int entityId, int targetId) {
        if (E(entityId).hasNPC()) {
            return E(targetId).isCharacter();
        }
        return E(targetId).isCharacter() || (E(targetId).hasNPC() && E(targetId).isAttackable());
    }

    @Override
    public int damageCalculation(int userId, int entityId) {
        E entity = E(userId);
        final Optional<Obj> obj = entity.hasWeapon() ? objectManager.getObject(entity.getWeapon().index) : Optional.empty();
        final Optional<WeaponObj> weapon =
                obj.isPresent() && Type.WEAPON.equals(obj.get().getType()) ? Optional.of((WeaponObj) obj.get()) : Optional.empty();

        int baseDamage = getBaseDamage(entity, weapon);
        Log.info("Base Damage: " + baseDamage);
        AttackPlace place = AttackPlace.getRandom();
        int defense = (place == AttackPlace.HEAD ? getHeadDefense(entityId) : getBodyDefense(entityId));
        Log.info("Defense: " + defense);
        return Math.max(0, baseDamage - defense);
    }

    private int getBodyDefense(int entityId) {
        int min = 0, max = 1;
        E entity = E(entityId);
        if (entity.hasArmor()) {
            int index = entity.getArmor().getIndex();
            ArmorObj armorObj = (ArmorObj) objectManager.getObject(index).get();
            min = armorObj.getMinDef();
            max = armorObj.getMaxDef();
        }
        if (entity.hasShield()) {
            int index = entity.getShield().index;
            ShieldObj shieldObj = (ShieldObj) objectManager.getObject(index).get();
            min += shieldObj.getMinDef();
            max += shieldObj.getMaxDef();
        }
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    private int getHeadDefense(int entityId) {
        int min = 0, max = 1;
        E entity = E(entityId);
        if (entity.hasHelmet()) {
            int index = entity.getHelmet().index;
            HelmetObj obj = (HelmetObj) objectManager.getObject(index).get();
            min = obj.getMinDef();
            max = obj.getMaxDef();
        }
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    private int getBaseDamage(E entity, Optional<WeaponObj> weapon) {
        int baseDamage = 0;
        if (entity.hasCharHero()) {
            CharClass clazz = CharClass.of(entity);
            AttackKind kind = AttackKind.getKind(entity);
            ThreadLocalRandom random = ThreadLocalRandom.current();
            float modifier = kind == AttackKind.PROJECTILE ?
                    Modifiers.PROJECTILE_DAMAGE.of(clazz) :
                    kind == AttackKind.WEAPON ? Modifiers.WEAPON_DAMAGE.of(clazz) : Modifiers.WRESTLING_DAMAGE.of(clazz);
            Log.info("Modifier: " + modifier);
            int weaponDamage =
                    weapon.map(weaponObj -> random.nextInt(weaponObj.getMinHit(), weaponObj.getMaxHit() + 1))
                            .orElseGet(() -> random.nextInt(4, 9));
            Log.info("Weapon Damage: " + weaponDamage);
            int maxWeaponDamage = weapon.map(WeaponObj::getMaxHit).orElse(9);
            Log.info("Max Weapon Damage: " + maxWeaponDamage);
            int userDamage = random.nextInt(entity.getHit().getMin() - 10, entity.getHit().getMax() + 1);
            Log.info("User damage: " + userDamage);
            baseDamage = (int) ((3 * weaponDamage + ((maxWeaponDamage) / 5) * Math.max(0, entity.strengthCurrentValue() - 15) + userDamage)
                    * modifier);
        } else if (entity.hasHit()) {
            baseDamage = ThreadLocalRandom.current().nextInt(Math.max(0, entity.getHit().getMin()), entity.getHit().getMax() + 1);
        }
        return baseDamage;
    }

    @Override
    Optional<Integer> getTarget(int userId) {
        E entity = E(userId);
        Heading headingTo = entity.getHeading();
        WorldPos worldPos = entity.getWorldPos();
        WorldPos targetPos = WorldUtils(world).getFacingPos(worldPos, headingTo);
        return mapManager
                .getNearEntities(userId)
                .stream()
                .filter(
                        targetId -> isEffectiveTarget(targetPos, targetId))
                .findFirst();
    }

    private boolean isEffectiveTarget(WorldPos targetPos, Integer targetId) {
        return E(targetId).hasWorldPos() && isAttackable(targetId) && (E(targetId).getWorldPos().equals(targetPos) || footprintOf(targetId, targetPos, System.currentTimeMillis()));
    }

    private boolean footprintOf(Integer entity, WorldPos worldPos, long timestamp) {
        final Set<Integer> footprints = mapManager.getEntitiesFootprints().get(entity);
        return footprints != null && footprints
                .stream()
                .anyMatch(footprint -> worldPos.equals(E(footprint).getWorldPos()) && timestamp - E(footprint).getFootprint().timestamp < TIME_TO_MOVE_1_TILE);
    }

    @Override
    void doHit(int userId, int entityId, int damage) {
        boolean userStab = canStab(userId);
        int result =
                userStab ?
                        doStab(userId, entityId, damage) :
                        canCriticAttack(userId, entityId) ?
                                doCriticAttack(userId, entityId, damage) :
                                doNormalAttack(userId, entityId, damage);

        EntityUpdate update = EntityUpdateBuilder.of(userId).withComponents(new AttackAnimation()).build();
        entityUpdateSystem.add(update, UpdateTo.ALL);

        notify(entityId, userStab ? CombatMessage.stab("" + damage) : CombatMessage.physic("" + damage));

        final E target = E(entityId);
        Health health = target.getHealth();
        int effectiveDamage = Math.min(health.min, result);
        characterTrainingSystem.userTakeDamage(userId, entityId, effectiveDamage);
        health.min = Math.max(0, health.min - result);
        sendFX(entityId);
        if (health.min > 0) {
            update(entityId);
            soundEntitySystem.add(userId, 10);
        } else {
            // TODO die
            characterTrainingSystem.takeGold(userId, entityId);
            notifyCombat(userId, Messages.KILL, getName(entityId));
            notifyCombat(entityId, Messages.KILLED, getName(userId));
            worldManager.entityDie(entityId);
            soundEntitySystem.add(userId, 126);
        }
    }

    private void notifyCombat(int userId, Messages message, String... messageParams) {
        final ConsoleMessage combat = ConsoleMessage.combat(message.name(), messageParams);
        messageSystem.add(userId, combat);
    }

    private int doNormalAttack(int userId, int entityId, int damage) {
        notifyCombat(userId, Messages.USER_NORMAL_HIT, getName(entityId), Integer.toString(damage));
        notifyCombat(entityId, Messages.VICTIM_NORMAL_HIT, getName(userId), Integer.toString(damage));
        return damage;
    }

    private int doCriticAttack(int userId, int entityId, int damage) {
        // TODO
        notifyCombat(userId, Messages.USER_CRITIC_HIT, getName(entityId), Integer.toString(damage));
        notifyCombat(entityId, Messages.VICTIM_CRITIC_HIT, getName(userId), Integer.toString(damage));
        return damage;
    }

    private boolean canCriticAttack(int userId, int entityId) {
        return false;
    }

    private boolean canStab(int userId) {
        final E e = E(userId);
        boolean result = false;
        if (e.hasWeapon()) {
            final Optional<Obj> object = objectManager.getObject(e.getWeapon().index);
            result = object
                    .filter(WeaponObj.class::isInstance)
                    .map(WeaponObj.class::cast)
                    .filter(WeaponObj::isStab)
                    .isPresent();
        }

        return result && stabProbability(userId);
    }

    private boolean stabProbability(int userId) {
        float skill = 100;
        int lucky;
        E e = E(userId);
        final CharClass clazz = CharClass.of(e);
        switch (clazz) {
            case ASSASSIN:
                lucky = (int) (((0.00003f * skill - 0.002) * skill + 0.098f) * skill + 4.25f);
                break;
            case CLERIC:
            case PALADIN:
            case PIRATE:
                lucky = (int) (((0.000003f * skill - 0.0006f) * skill + 0.0107f) * skill + 4.93f);
                break;
            case BARDIC:
                lucky = (int) (((0.000002f * skill - 0.0002f) * skill + 0.032f) * skill + 4.81f);
                break;
            default:
                lucky = (int) (0.0361f * skill + 4.39f);
                break;

        }
        return ThreadLocalRandom.current().nextInt(101) < lucky;
    }

    private int doStab(int userId, int entityId, int damage) {
        final CharClass clazz = CharClass.of(E(userId));
        damage += (int) (CharClass.ASSASSIN.equals(clazz) ? damage * ASSASIN_STAB_FACTOR : damage * NORMAL_STAB_FACTOR);
        notifyCombat(userId, Messages.USER_STAB_HIT, getName(entityId), Integer.toString(damage));
        notifyCombat(entityId, Messages.VICTIM_STAB_HIT, getName(userId), Integer.toString(damage));
        return damage;
    }

    private String getName(int userId) {
        return E(userId).getName().text;
    }

    @Override
    boolean isAttackable(int entityId) {
        return E(entityId).hasNPC() || E(entityId).isCharacter();
    }

    /**
     * Send combat notification to user and near by entities
     *
     * @param victim        component.entity id
     * @param combatMessage message
     */
    private void notify(int victim, CombatMessage combatMessage) {
        EntityUpdate update = EntityUpdateBuilder.of(victim).withComponents(combatMessage).build();
        entityUpdateSystem.add(update, UpdateTo.ALL);
    }

    /**
     * Send an update to component.entity with current health
     *
     * @param victim component.entity id
     */
    private void update(int victim) {
        E v = E(victim);
        EntityUpdate update = EntityUpdateBuilder.of(victim).withComponents(v.getHealth()).build();
        entityUpdateSystem.add(update, UpdateTo.ENTITY);
    }

    private void sendFX(int victim) {
        effectEntitySystem.addFX(victim, FXs.FX_BLOOD, 1);
    }

    private enum AttackKind {
        WEAPON,
        PROJECTILE,
        WRESTLING;

        protected static AttackKind getKind(E entity) {
            return entity.hasWeapon() ? WEAPON : WRESTLING;
        }
    }

    private enum AttackPlace {
        HEAD,
        BODY;

        private static final List<AttackPlace> VALUES = Arrays.asList(values());
        private static final int SIZE = VALUES.size();
        private static final Random RANDOM = new Random();

        public static AttackPlace getRandom() {
            return VALUES.get(RANDOM.nextInt(SIZE));
        }
    }
}
