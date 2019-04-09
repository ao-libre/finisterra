package server.combat;

import com.artemis.Component;
import com.artemis.E;
import com.esotericsoftware.minlog.Log;
import entity.CombatMessage;
import entity.Heading;
import entity.character.status.Health;
import physics.AttackAnimation;
import position.WorldPos;
import server.core.Server;
import server.database.model.attributes.Attributes;
import server.database.model.modifiers.Modifiers;
import server.manager.IManager;
import shared.interfaces.CharClass;
import shared.interfaces.FXs;
import shared.interfaces.Race;
import shared.network.notifications.EntityUpdate;
import shared.network.notifications.FXNotification;
import shared.objects.types.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.artemis.E.E;
import static server.utils.WorldUtils.WorldUtils;

public class PhysicalCombatSystem extends AbstractCombatSystem implements IManager {

    private static final String MISS = "MISS";

    private static final String USER_CRITIC_HIT = "Has golpeado criticamente a %s por %d";
    private static final String VICTIM_CRITIC_HIT = "%s te ha golpeado criticamente por por %d";

    private static final String USER_STAB_HIT = "Has apuñalado a %s por %d";
    private static final String VICTIM_STAB_HIT = "%s te ha apuñalado por %d";

    private static final String USER_NORMAL_HIT = "Has golpeado a %s por %d";
    private static final String VICTIM_NORMAL_HIT = "%s te ha golpeado por %d";

    public PhysicalCombatSystem(Server server) {
        super(server);
    }

    @Override
    protected void failed(int entityId) {
        notify(entityId, new CombatMessage(MISS));
    }

    @Override
    public boolean canAttack(int userId, Optional<Integer> targetId) {
        // TODO estas muerto
        // TODO no podes atacar un muerto
        // TODO es del otro team?
        if (targetId.isPresent()) {
            // TODO attack power can be bow
            int evasionPower = evasionPower(targetId.get()) + shieldEvasionPower(targetId.get());
            double prob = Math.max(10, Math.min(90, 50 + (weaponAttackPower(userId) - evasionPower) * 0.4));
            if (ThreadLocalRandom.current().nextInt(101) <= prob) {
                return true;
            } else {
                // calculate if was evaded by shield
            }
        }
        return false;
    }

    @Override
    public int damageCalculation(int userId, int entityId) {
        E entity = E(userId);
        final Optional<Obj> obj = getServer().getObjectManager().getObject(entity.getWeapon().index);
        final Optional<WeaponObj> weapon = obj.isPresent() && Type.WEAPON.equals(obj.get().getType()) ? Optional.of((WeaponObj) obj.get()) : Optional.empty();

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
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    private int getBaseDamage(E entity, Optional<WeaponObj> weapon) {
        CharClass clazz = CharClass.get(entity);
        Race race = Race.of(entity);
        AttackKind kind = AttackKind.getKind(entity);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        float modifier = kind == AttackKind.PROJECTILE ? Modifiers.PROJECTILE.of(clazz) : kind == AttackKind.WEAPON ? Modifiers.WEAPON.of(clazz) : Modifiers.WRESTLING.of(clazz);
        Log.info("Modifier: " + modifier);
        int weaponDamage = weapon.isPresent() ? random.nextInt(weapon.get().getMinHit(), weapon.get().getMaxHit()) : random.nextInt(4, 9);
        Log.info("Weapon Damage: " + weaponDamage);
        int maxWeaponDamage = weapon.isPresent() ? weapon.get().getMaxHit() : 9;
        Log.info("Max Weapon Damage: " + maxWeaponDamage);
        int userDamage = random.nextInt(entity.getHit().getMin() - 10, entity.getHit().getMax() + 1);
        Log.info("User damage: " + userDamage);
        return (int) ((3 * weaponDamage + ((maxWeaponDamage) / 5) * Math.max(0, entity.strengthValue() - 15) + userDamage) * modifier);
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
                .filter(targetId -> E(targetId).hasWorldPos() && E(targetId).getWorldPos().equals(targetPos) && isAttackable(targetId))
                .findFirst();
    }

    @Override
    void doHit(int userId, int entityId, int damage) {
        AttackResult result =
                canStab(userId, entityId) ?
                        doStab(userId, entityId, damage) :
                        canCriticAttack(userId, entityId) ?
                                doCrititAttack(userId, entityId, damage) :
                                doNormalAttack(userId, entityId, damage);

        // TODO send console messages
        getServer().getWorldManager().notifyUpdate(userId, new EntityUpdate(userId, new Component[]{new AttackAnimation()}, new Class[0]));
        notify(entityId, new CombatMessage("-" + result.damage));

        Health health = E(entityId).getHealth();
        health.min = Math.max(0, health.min - damage);
        if (health.min > 0) {
            update(entityId);
        } else {
            // TODO die
        }
    }

    private AttackResult doNormalAttack(int userId, int entityId, int damage) {
        return new AttackResult(damage, String.format(USER_NORMAL_HIT, getName(entityId), damage), String.format(VICTIM_NORMAL_HIT, getName(userId), damage));
    }

    private AttackResult doCrititAttack(int userId, int entityId, int damage) {
        // TODO
        return new AttackResult(damage, String.format(USER_CRITIC_HIT, getName(entityId), damage), String.format(VICTIM_CRITIC_HIT, getName(userId), damage));
    }

    private boolean canCriticAttack(int userId, int entityId) {
        return false;
    }

    private boolean canStab(int userId, int entityId) {
        // TODO
        return false;
    }

    private AttackResult doStab(int userId, int entityId, int damage) {
        // TODO
        return new AttackResult(damage, String.format(USER_STAB_HIT, getName(entityId), damage), String.format(VICTIM_STAB_HIT, getName(userId), damage));
    }

    private String getName(int userId) {
        return E(userId).getName().text;
    }

    @Override
    boolean isAttackable(int entityId) {
        return true;
    }

    /**
     * Send combat notification to user and near by entities
     *
     * @param victim        entity id
     * @param combatMessage message
     */
    public void notify(int victim, CombatMessage combatMessage) {
        EntityUpdate update = new EntityUpdate(victim, new Component[]{combatMessage}, new Class[0]);
        getServer().getWorldManager().sendEntityUpdate(victim, update);
        getServer().getWorldManager().notifyToNearEntities(victim, update);
    }

    /**
     * Send an update to entity with current health
     *
     * @param victim entity id
     */
    void update(int victim) {
        EntityUpdate update = new EntityUpdate(victim, new Component[]{E(victim).getHealth()}, new Class[0]);
        getServer().getWorldManager().sendEntityUpdate(victim, update);
        getServer().getWorldManager().notifyUpdate(victim, new FXNotification(victim, FXs.FX_BLOOD));
    }

    private static class AttackResult {
        private final int damage;
        private final String userMessage;
        private String victimMessage;

        public AttackResult(int damage, String userMessage, String victimMessage) {

            this.damage = damage;
            this.userMessage = userMessage;
            this.victimMessage = victimMessage;
        }

        public int getDamage() {
            return damage;
        }

        public String getUserMessage() {
            return userMessage;
        }

        public String getVictimMessage() {
            return victimMessage;
        }
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
}
