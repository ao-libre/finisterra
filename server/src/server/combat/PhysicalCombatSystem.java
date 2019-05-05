package server.combat;

import com.artemis.Component;
import com.artemis.E;
import com.esotericsoftware.minlog.Log;
import entity.character.states.Heading;
import entity.character.status.Health;
import entity.world.CombatMessage;
import physics.AttackAnimation;
import position.WorldPos;
import server.core.Server;
import server.database.model.modifiers.Modifiers;
import server.manager.IManager;
import server.manager.WorldManager;
import shared.interfaces.CharClass;
import shared.interfaces.FXs;
import shared.interfaces.Race;
import shared.network.notifications.ConsoleMessage;
import shared.network.notifications.EntityUpdate;
import shared.network.notifications.FXNotification;
import shared.network.sound.SoundNotification;
import shared.objects.types.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.artemis.E.E;
import static java.lang.String.format;
import static server.utils.WorldUtils.WorldUtils;
import static shared.util.Messages.*;

public class PhysicalCombatSystem extends AbstractCombatSystem implements IManager {

    private static final String MISS = "MISS";
    private static final float ASSASIN_STAB_FACTOR = 1.5f;
    private static final float NORMAL_STAB_FACTOR = 1.4f;

    public PhysicalCombatSystem(Server server) {
        super(server);
    }

    @Override
    protected void failed(int entityId, Optional<Integer> targetId) {
        notify(targetId.isPresent() ? targetId.get() : entityId, CombatMessage.physic(MISS));
    }

    @Override
    public boolean canAttack(int userId, Optional<Integer> targetId) {
        // estas muerto
        final E e = E(userId);
        if (e != null && e.hasHealth() && e.getHealth().min == 0) {
            notifyCombat(userId, DEAD_CANT_ATTACK);
            return false;
        }

        if (targetId.isPresent()) {
            // no podes atacar un muerto
            E t = E(targetId.get());
            if (t == null) {
                Log.info("Can't find target");
                return false;
            }
            if (t.hasHealth() && t.getHealth().min == 0) {
                notifyCombat(userId, CANT_ATTACK_DEAD);
                return false;
            }

            // es del otro team? ciuda - crimi
            if (!e.isCriminal() && !t.isCriminal() /* TODO agregar seguro */) {
                // notifyCombat(userId, CANT_ATTACK_CITIZEN);
                // TODO descomentar: return false;
            }

            // TODO attack power can be bow

            int evasionPower = evasionPower(targetId.get()) + (E(targetId.get()).hasShield() ? shieldEvasionPower(targetId.get()) : 0);
            double prob = Math.max(10, Math.min(90, 50 + (weaponAttackPower(userId) - evasionPower) * 0.4));
            if (ThreadLocalRandom.current().nextInt(101) <= prob) {
                return true;
            } else {
                int skills = 200;
                prob = Math.max(10, Math.min(90, 100 * 100 / skills));

                // shield evasion
                if (E(targetId.get()).hasShield() && ThreadLocalRandom.current().nextInt(101) <= prob) {
                    notifyCombat(targetId.get(), SHIELD_DEFENSE);
                    notifyCombat(userId, format(DEFENDED_WITH_SHIELD, getName(targetId.get())));
                    // TODO sound
                    // TODO shield animation
                    getWorldManager().notifyUpdate(targetId.get(), new SoundNotification(10));
                } else {
                    notifyCombat(userId, ATTACK_FAILED);
                    notifyCombat(targetId.get(), format(ATTACKED_AND_FAILED, getName(userId)));

                }
            }
        }
        return false;
    }

    @Override
    public int damageCalculation(int userId, int entityId) {
        E entity = E(userId);
        final Optional<Obj> obj = getServer().getObjectManager().getObject(entity.getWeapon().index);
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
            ArmorObj armorObj = (ArmorObj) getServer().getObjectManager().getObject(index).get();
            min = armorObj.getMinDef();
            max = armorObj.getMaxDef();
        }
        if (entity.hasShield()) {
            int index = entity.getShield().index;
            ShieldObj shieldObj = (ShieldObj) getServer().getObjectManager().getObject(index).get();
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
            HelmetObj obj = (HelmetObj) getServer().getObjectManager().getObject(index).get();
            min = obj.getMinDef();
            max = obj.getMaxDef();
        }
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    private int getBaseDamage(E entity, Optional<WeaponObj> weapon) {
        CharClass clazz = CharClass.get(entity);
        Race race = Race.of(entity);
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
        return (int) ((3 * weaponDamage + ((maxWeaponDamage) / 5) * Math.max(0, entity.strengthValue() - 15) + userDamage)
                * modifier);
    }

    @Override
    Optional<Integer> getTarget(int userId) {
        E entity = E(userId);
        Heading headingTo = entity.getHeading();
        WorldPos worldPos = entity.getWorldPos();
        WorldPos targetPos = WorldUtils(getServer().getWorld()).getFacingPos(worldPos, headingTo);
        return getServer()
                .getMapManager()
                .getNearEntities(userId)
                .stream()
                .filter(
                        targetId -> E(targetId).hasWorldPos() && E(targetId).getWorldPos().equals(targetPos) && isAttackable(targetId))
                .findFirst();
    }

    @Override
    void doHit(int userId, int entityId, int damage) {
        boolean userStab = canStab(userId);
        AttackResult result =
                userStab ?
                        doStab(userId, entityId, damage) :
                        canCriticAttack(userId, entityId) ?
                                doCrititAttack(userId, entityId, damage) :
                                doNormalAttack(userId, entityId, damage);

        // TODO send console messages
        notifyCombat(userId, result.userMessage);
        notifyCombat(entityId, result.victimMessage);

        getWorldManager()
                .notifyUpdate(userId, new EntityUpdate(userId, new Component[]{new AttackAnimation()}, new Class[0]));
        notify(entityId, userStab ? CombatMessage.stab("" + result.damage) : CombatMessage.physic("" + result.damage));

        final E target = E(entityId);
        Health health = target.getHealth();
        health.min = Math.max(0, health.min - result.damage);
        if (health.min > 0) {
            update(entityId);
        } else {
            // TODO die
            notifyCombat(userId, format(KILL, getName(entityId)));
            notifyCombat(entityId, format(KILLED, getName(userId)));
            getWorldManager().userDie(entityId);
        }
    }

    private void notifyCombat(int userId, String message) {
        final ConsoleMessage combat = ConsoleMessage.combat(message);
        getWorldManager().sendEntityUpdate(userId, combat);
    }

    private WorldManager getWorldManager() {
        return getServer().getWorldManager();
    }

    private AttackResult doNormalAttack(int userId, int entityId, int damage) {
        return new AttackResult(damage, format(USER_NORMAL_HIT, getName(entityId), damage),
                format(VICTIM_NORMAL_HIT, getName(userId), damage));
    }

    private AttackResult doCrititAttack(int userId, int entityId, int damage) {
        // TODO
        return new AttackResult(damage, format(USER_CRITIC_HIT, getName(entityId), damage),
                format(VICTIM_CRITIC_HIT, getName(userId), damage));
    }

    private boolean canCriticAttack(int userId, int entityId) {
        return false;
    }

    private boolean canStab(int userId) {
        final E e = E(userId);
        boolean result = false;
        if (e.hasWeapon()) {
            final Optional<Obj> object = getServer().getObjectManager().getObject(e.getWeapon().index);
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
        final CharClass clazz = CharClass.get(e);
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

    private AttackResult doStab(int userId, int entityId, int damage) {
        final CharClass clazz = CharClass.get(E(userId));
        damage += (int) (CharClass.ASSASSIN.equals(clazz) ? damage * ASSASIN_STAB_FACTOR : damage * NORMAL_STAB_FACTOR);
        return new AttackResult(damage, format(USER_STAB_HIT, getName(entityId), damage),
                format(VICTIM_STAB_HIT, getName(userId), damage));
    }

    private String getName(int userId) {
        return E(userId).getName().text;
    }

    @Override
    boolean isAttackable(int entityId) {
        return E(entityId).hasWorldPos();
    }

    /**
     * Send combat notification to user and near by entities
     *
     * @param victim        entity id
     * @param combatMessage message
     */
    private void notify(int victim, CombatMessage combatMessage) {
        EntityUpdate update = new EntityUpdate(victim, new Component[]{combatMessage}, new Class[0]);
        getServer().getWorldManager().sendEntityUpdate(victim, update);
        getServer().getWorldManager().notifyToNearEntities(victim, update);
    }

    /**
     * Send an update to entity with current health
     *
     * @param victim entity id
     */
    private void update(int victim) {
        EntityUpdate update = new EntityUpdate(victim, new Component[]{E(victim).getHealth()}, new Class[0]);
        getServer().getWorldManager().sendEntityUpdate(victim, update);
        getServer().getWorldManager().notifyUpdate(victim, new FXNotification(victim, FXs.FX_BLOOD));
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

        private static final List<AttackPlace> VALUES =
                Collections.unmodifiableList(Arrays.asList(values()));
        private static final int SIZE = VALUES.size();
        private static final Random RANDOM = new Random();

        public static AttackPlace getRandom() {
            return VALUES.get(RANDOM.nextInt(SIZE));
        }
    }

    private static class AttackResult {

        private final int damage;
        private final String userMessage;
        private String victimMessage;

        AttackResult(int damage, String userMessage, String victimMessage) {

            this.damage = damage;
            this.userMessage = userMessage;
            this.victimMessage = victimMessage;
        }

    }
}
