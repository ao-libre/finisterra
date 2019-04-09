package server.combat;

import server.core.Server;
import java.util.Optional;

public abstract class AbstractCombatSystem implements CombatSystem {

    private final Server server;

    public AbstractCombatSystem(Server server) {
        this.server = server;
    }

    public Server getServer() {
        return server;
    }

    @Override
    public int shieldEvasionPower(int userId) {
        return 0;
    }

    @Override
    public int evasionPower(int userId) {
        return 0;
    }

    @Override
    public int weaponAttackPower(int userId) {
        return 0;
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
    public void userAttack(int userId, Optional<Integer> targetId) {
        Optional<Integer> realTargetId = targetId.isPresent() ? targetId : getTarget(userId);
        if (canAttack(userId, realTargetId)) {
            hit(userId, realTargetId.get());
        } else {
            failed(userId);
        }
    }

    protected abstract void failed(int entityId);

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

}
