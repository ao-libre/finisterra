package server.combat;

import com.artemis.BaseSystem;
import com.artemis.E;
import entity.character.status.Stamina;
import server.core.Server;
import server.database.model.modifiers.Modifiers;
import server.systems.manager.WorldManager;
import shared.interfaces.CharClass;
import shared.network.notifications.EntityUpdate;
import shared.network.notifications.EntityUpdate.EntityUpdateBuilder;

import java.util.Optional;

import static com.artemis.E.E;

public abstract class AbstractCombatSystem extends BaseSystem implements CombatSystem {

    public static final int STAMINA_REQUIRED_PERCENT = 15;
    private final Server server;

    public AbstractCombatSystem(Server server) {
        this.server = server;
    }

    public Server getServer() {
        return server;
    }

    @Override
    public int shieldEvasionPower(int userId) {
        E e = E(userId);
        float shieldModifier = Modifiers.SHIELD.of(CharClass.get(e));
        return (int) (100 * shieldModifier / 2);
    }

    @Override
    public int evasionPower(int userId) {
        E e = E(userId);
        int power = 0;
        if (e.hasCharHero()) {
            float temp = 100 + 100 / 33 * e.getAgility().getBaseValue() * Modifiers.EVASION.of(CharClass.get(e));
            power = (int) (temp + 2.5f * Math.max(e.getLevel().level - 12, 0));
        } else if (e.hasEvasionPower()) {
            power = e.getEvasionPower().value;
        }
        return power;
    }

    @Override
    public int weaponAttackPower(int userId) {
        E e = E(userId);
        int power = 0;
        if (e.hasCharHero()) {
            power = (int) (100 + 3 * e.getAgility().getBaseValue() * Modifiers.WEAPON.of(CharClass.get(e)));
        } else if (e.hasAttackPower()){
            power = e.getAttackPower().value;
        }
        return power;
    }

    @Override
    public int projectileAttackPower(int userId) {
        return 0;
    }

    @Override
    public int wrestlingAttackPower(int userId) {
        return 0;
    }

    @Override
    public void entityAttack(int entityId, Optional<Integer> targetId) {
        Optional<Integer> realTargetId = targetId.isPresent() ? targetId : getTarget(entityId);
        if (canAttack(entityId, realTargetId)) {
            hit(entityId, realTargetId.get());
        } else {
            failed(entityId, realTargetId);
        }
        E e = E(entityId);
        if (e.hasStamina()) {
            Stamina stamina = e.getStamina();
            stamina.min = Math.max(0, stamina.min - stamina.max * STAMINA_REQUIRED_PERCENT / 100);
            EntityUpdate update = EntityUpdateBuilder.of(entityId).withComponents(stamina).build();
            getWorld().getSystem(WorldManager.class).sendEntityUpdate(entityId, update);
        }
    }

    protected abstract void failed(int entityId, Optional<Integer> targetId);

    @Override
    public void hit(int userId, int entityId) {
        int damage = damageCalculation(userId, entityId);
        doHit(userId, entityId, damage);
    }

    @Override
    public abstract boolean canAttack(int userId, Optional<Integer> targetId);

    @Override
    public abstract int damageCalculation(int userId, int entityId);

    abstract Optional<Integer> getTarget(int userId);

    abstract void doHit(int userId, int entityId, int damage);

    abstract boolean isAttackable(int entityId);

    @Override
    protected void processSystem() {}
}
