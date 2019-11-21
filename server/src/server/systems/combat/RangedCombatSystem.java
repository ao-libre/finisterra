package server.systems.combat;

import com.artemis.E;
import com.artemis.annotations.Wire;
import com.esotericsoftware.minlog.Log;
import entity.character.status.Health;
import entity.character.status.Stamina;
import entity.world.CombatMessage;
import graphics.Effect;
import physics.AttackAnimation;
import position.WorldPos;
import server.database.model.modifiers.Modifiers;
import server.systems.CharacterTrainingSystem;
import server.systems.manager.MapManager;
import server.systems.manager.ObjectManager;
import server.systems.manager.WorldManager;
import shared.interfaces.CharClass;
import shared.interfaces.FXs;
import shared.network.combat.AttackRequest;
import shared.network.notifications.ConsoleMessage;
import shared.network.notifications.EntityUpdate;
import shared.network.notifications.EntityUpdate.EntityUpdateBuilder;
import shared.network.sound.SoundNotification;
import shared.objects.types.*;
import shared.util.Messages;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.artemis.E.E;



@Wire
public class RangedCombatSystem extends AbstractCombatSystem {

    private Optional<Integer> target;
    private static final int TIME_TO_MOVE_1_TILE = 200;

    // Injected Systems
    private MapManager mapManager;
    private WorldManager worldManager;
    private ObjectManager objectManager;
    private CharacterTrainingSystem characterTrainingSystem;

    public void shoot(int userId, AttackRequest attackRequest) {
        final WorldPos targetPos = attackRequest.getWorldPos();
        final long timestamp = attackRequest.getTimestamp();
        target = getTargetx(userId, targetPos, timestamp);

        if (!target.isPresent ( )) {
            energyUse ( userId );
            worldManager.notifyUpdate ( userId, EntityUpdateBuilder.of ( userId ).withComponents ( new AttackAnimation ( ) ).build ( ) );
            final ConsoleMessage combat = ConsoleMessage.combat(Messages.ATTACK_FAILED);
            worldManager.sendEntityUpdate(userId, combat);
            notify(userId, CombatMessage.physic( "Fallas!"));
        }else
        if (canAttack ( userId, target )) {
            energyUse ( userId );
            worldManager.notifyUpdate ( userId, EntityUpdateBuilder.of ( userId ).withComponents ( new AttackAnimation ( ) ).build ( ) );
            doHit ( userId, target.get ( ), damageCalculation ( userId, target.get ( ) ) );
        }
    }

    private void energyUse(int userId) {
        E e = E(userId);
        Stamina stamina = e.getStamina();
        stamina.min = Math.max(0, stamina.min - stamina.max * STAMINA_REQUIRED_PERCENT / 120);
        EntityUpdate update = EntityUpdateBuilder.of(userId).withComponents(stamina).build();
        getWorld().getSystem(WorldManager.class).sendEntityUpdate(userId, update);
        getWorldManager ( ).notifyUpdate ( userId, new SoundNotification ( 65 ) );
    }


    Optional<Integer> getTargetx(int userId, WorldPos worldPos, long timestamp) {
        Set<Integer> entities = new HashSet<>(mapManager.getNearEntities(userId));
        entities.add(userId);
        return entities
                .stream()
                .map(E::E)
                .filter(Objects::nonNull)
                .filter(E::hasWorldPos)
                .filter(e -> !e.hasObject())
                .filter(entity -> isValidTarget(worldPos, timestamp, entity))
                .map(E::id)
                .findFirst();
    }
    @Override
    protected void failed(int entityId, Optional<Integer> targetId) {
        notifyCombat(targetId.orElse(entityId), Messages.ATTACK_FAILED);
    }
    private boolean isValidTarget(WorldPos worldPos, long timestamp, E entity) {
        return entity.getWorldPos().equals(worldPos) || footprintOf(entity.id(), worldPos, timestamp);
    }

    private boolean footprintOf(Integer entity, WorldPos worldPos, long timestamp) {
        final Set<Integer> footprints = mapManager.getEntitiesFootprints().get(entity);
        return footprints != null && footprints
                .stream()
                .anyMatch(footprint -> worldPos.equals(E(footprint).getWorldPos()) && (timestamp - E(footprint).getFootprint().timestamp <= TIME_TO_MOVE_1_TILE));
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
        if (e.getName().text.equals ( getName ( target.get () ) )){
            notifyCombat ( entityId, Messages.CANT_ATTACK_YOURSELF);
            return false;
        }

        if (target.isPresent()) {
            int targetId = target.get();
            E t = E(targetId);
            if (t == null) {
                Log.info("Can't find target");
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
            double prob = Math.max(10, Math.min(90, 50 + (projectileAttackPower (entityId) - evasionPower) * 0.4));
            if (ThreadLocalRandom.current().nextInt(101) <= prob) {
                return true;
            } else {
                energyUse ( entityId );
                int skills = 200;
                prob = Math.max(10, Math.min(90, 100 * 100 / skills));

                // shield evasion
                if (E(targetId).hasShield() && ThreadLocalRandom.current().nextInt(101) <= prob) {
                    notifyCombat(targetId, Messages.SHIELD_DEFENSE);
                    notifyCombat(entityId, Messages.DEFENDED_WITH_SHIELD, getName(targetId));
                    // TODO shield animation
                    worldManager.notifyUpdate(targetId, new SoundNotification (37));
                } else {
                    notifyCombat(entityId, Messages.ATTACK_FAILED);
                    notifyCombat(targetId, Messages.ATTACKED_AND_FAILED, getName(entityId));

                }
            }
        }
        return false;
    }
    @Override
    public int damageCalculation(int userId, int entityId) {
        E entity = E(userId);
        final Optional<Obj> obj = entity.hasWeapon() ? objectManager.getObject(entity.getWeapon().index) : Optional.empty();
        final Optional< WeaponObj > weapon =
                obj.isPresent() && Type.WEAPON.equals(obj.get().getType()) ? Optional.of((WeaponObj) obj.get()) : Optional.empty();

        int baseDamage = getBaseDamage(entity, weapon);
        Log.info("Base Damage: " + baseDamage);
        RangedCombatSystem.AttackPlace place = RangedCombatSystem.AttackPlace.getRandom();
        int defense = (place == RangedCombatSystem.AttackPlace.HEAD ? getHeadDefense(entityId) : getBodyDefense(entityId));
        Log.info("Defense: " + defense);
        return Math.max(0, baseDamage - defense);
    }

    @Override
    Optional< Integer > getTarget(int userId) {
        return target;
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
            RangedCombatSystem.AttackKind kind = RangedCombatSystem.AttackKind.getKind(entity);
            ThreadLocalRandom random = ThreadLocalRandom.current();
            float modifier = kind == RangedCombatSystem.AttackKind.PROJECTILE ?
                    Modifiers.PROJECTILE_DAMAGE.of(clazz) :
                    kind == RangedCombatSystem.AttackKind.WEAPON ? Modifiers.WEAPON_DAMAGE.of(clazz) : Modifiers.WRESTLING_DAMAGE.of(clazz);
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

    void doHit(int userId, int entityId, int damage) {
        int result =
                canCriticAttack(userId, entityId) ?
                        doCriticAttack(userId, entityId, damage) :
                        doNormalAttack(userId, entityId, damage);

        worldManager.notifyUpdate(userId, EntityUpdateBuilder.of(userId).withComponents(new AttackAnimation()).build());
        notify(entityId, CombatMessage.physic(" " + damage));

        final E target = E(entityId);
        Health health = target.getHealth();
        int effectiveDamage = Math.min(health.min, result);
        characterTrainingSystem.userTakeDamage(userId, entityId, effectiveDamage);
        health.min = Math.max(0, health.min - result);
        sendFX(entityId);
        if (health.min > 0) {
            update(entityId);
            getWorldManager ( ).notifyUpdate ( userId, new SoundNotification ( 10 ) );
        } else {
            // TODO die
            characterTrainingSystem.takeGold(userId, entityId);
            notifyCombat(userId, Messages.KILL, getName(entityId));
            notifyCombat(entityId, Messages.KILLED, getName(userId));
            worldManager.entityDie(entityId);
            getWorldManager ( ).notifyUpdate ( userId, new SoundNotification ( 126 ) );
        }
    }

    @Override
    boolean isAttackable(int entityId) {
        return false;
    }

    private void notifyCombat(int userId, Messages message, String... messageParams) {
        final ConsoleMessage combat = ConsoleMessage.combat(message, messageParams);
        worldManager.sendEntityUpdate(userId, combat);
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

    /**
     * Send combat notification to user and near by entities
     *
     * @param victim        entity id
     * @param combatMessage message
     */
    private void notify(int victim, CombatMessage combatMessage) {
        worldManager.notifyUpdate(victim, EntityUpdateBuilder.of(victim).withComponents(combatMessage).build());
    }

    /**
     * Send an update to entity with current health
     *
     * @param victim entity id
     */
    private void update(int victim) {
        E v = E(victim);
        EntityUpdate update = EntityUpdateBuilder.of(victim).withComponents(v.getHealth()).build();
        worldManager.sendEntityUpdate(victim, update);
    }

    private void sendFX(int victim) {
        E v = E(victim);
        int fxE = world.create();
        EntityUpdateBuilder fxUpdate = EntityUpdateBuilder.of(fxE);
        Effect effect = new Effect.EffectBuilder().attachTo(victim).withLoops(1).withFX( FXs.FX_BLOOD).build();
        fxUpdate.withComponents(effect);
        if (v.hasWorldPos()) {
            WorldPos worldPos = v.getWorldPos();
            fxUpdate.withComponents(worldPos);
        }
        worldManager.notifyUpdate(victim, fxUpdate.build());
        world.delete(fxE);
    }

    private enum AttackKind {
        WEAPON,
        PROJECTILE,
        WRESTLING;

        protected static RangedCombatSystem.AttackKind getKind(E entity) {
            return entity.hasWeapon() ? WEAPON : WRESTLING;
        }
    }


    private enum AttackPlace {
        HEAD,
        BODY;

        private static final List< RangedCombatSystem.AttackPlace > VALUES = List.of(values());
        private static final int SIZE = VALUES.size();
        private static final Random RANDOM = new Random();

        public static RangedCombatSystem.AttackPlace getRandom() {
            return VALUES.get(RANDOM.nextInt(SIZE));
        }
    }

    private void notifyInfo(int userId, Messages messageId, String... messageParams) {
        final ConsoleMessage combat = ConsoleMessage.info(messageId, messageParams);
        getWorldManager().sendEntityUpdate(userId, combat);
    }

    private String getName(int userId) {
        return E(userId).getName().text;
    }

    private WorldManager getWorldManager() {
        return world.getSystem(WorldManager.class);
    }

    @Override
    protected void processSystem() {

    }
}
