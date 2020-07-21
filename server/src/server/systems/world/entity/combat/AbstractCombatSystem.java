package server.systems.world.entity.combat;

import com.artemis.BaseSystem;
import com.artemis.E;
import com.artemis.annotations.Wire;
import component.entity.character.status.Stamina;
import server.systems.network.EntityUpdateSystem;
import server.systems.world.WorldEntitiesSystem;
import server.systems.world.entity.factory.SoundEntitySystem;
import server.systems.world.entity.user.ModifierSystem;
import server.utils.UpdateTo;
import shared.interfaces.CharClass;
import shared.network.notifications.EntityUpdate;
import shared.util.EntityUpdateBuilder;

import java.util.Optional;

import static com.artemis.E.E;
import static server.database.model.modifiers.Modifiers.*;

@Wire(injectInherited = true)
public abstract class AbstractCombatSystem extends BaseSystem implements CombatSystem {

    public static final int STAMINA_REQUIRED_PERCENT = 15;
    protected EntityUpdateSystem entityUpdateSystem;
    protected SoundEntitySystem soundEntitySystem;
    protected ModifierSystem modifierSystem;

    @Override
    public int shieldEvasionPower(int userId) {
        E userEntity = E(userId);
        float shieldModifier = modifierSystem.of(SHIELD, CharClass.of(userEntity));
        return (int) (100 * shieldModifier / 2);
    }

    @Override
    public int evasionPower(int userId) {
        E userEntity = E(userId);
        int power = 0;
        if (userEntity.hasCharHero()) {
            float temp = 100 + 100 / 33 * userEntity.getAgility().getBaseValue() * modifierSystem.of(EVASION, CharClass.of(userEntity));
            power = (int) (temp + 2.5f * Math.max(userEntity.getLevel().level - 12, 0));
        } else if (userEntity.hasEvasionPower()) {
            power = userEntity.getEvasionPower().value;
        }
        return power;
    }

    @Override
    public int weaponAttackPower(int userId) {
        E userEntity = E(userId);
        int power = 0;
        if (userEntity.hasCharHero()) {
            power = (int) (100 + 3 * userEntity.getAgility().getBaseValue() * modifierSystem.of(WEAPON, CharClass.of(userEntity)));
        } else if (userEntity.hasAttackPower()) {
            power = userEntity.getAttackPower().value;
        }
        return power;
    }

    @Override
    public int projectileAttackPower(int userId) {
        E userEntity = E(userId);
        int power = 0;
        if (userEntity.hasCharHero()) {
            power = (int) (100 + 3 * userEntity.getAgility().getBaseValue() * modifierSystem.of(PROJECTILE, CharClass.of(userEntity)));
        } else if (userEntity.hasAttackPower()) {
            power = userEntity.getAttackPower().value;
        }
        return power;
    }

    @Override
    public int wrestlingAttackPower(int userId) {
        return 0;
    }

    @Override
    public void entityAttack(int entityId, Optional<Integer> targetId) {
        Optional<Integer> realTargetId = targetId.isPresent() ? targetId : getTarget(entityId);
        soundEntitySystem.add(entityId, 2);
        if (canAttack(entityId, realTargetId)) {
            hit(entityId, realTargetId.get());
        } else {
            failed(entityId, realTargetId);
        }
        E userEntity = E(entityId);
        if (userEntity.hasStamina()) {
            Stamina stamina = userEntity.getStamina();
            stamina.min = Math.max(0, stamina.min - stamina.max * STAMINA_REQUIRED_PERCENT / 100);
            EntityUpdate update = EntityUpdateBuilder.of(entityId).withComponents(stamina).build();
            entityUpdateSystem.add(update, UpdateTo.ENTITY);
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
    protected void processSystem() {
    }

    private WorldEntitiesSystem getWorldManager() {
        return world.getSystem(WorldEntitiesSystem.class);
    }
}
