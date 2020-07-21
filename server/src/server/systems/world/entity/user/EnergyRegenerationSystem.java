package server.systems.world.entity.user;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import component.entity.character.status.Stamina;
import server.systems.network.EntityUpdateSystem;
import server.systems.world.IntervalFluidIteratingSystem;
import server.utils.UpdateTo;
import shared.network.notifications.EntityUpdate;
import shared.util.EntityUpdateBuilder;

@Wire
public class EnergyRegenerationSystem extends IntervalFluidIteratingSystem {

    public static final int REGENERATION_PERCENT = 10;
    private EntityUpdateSystem entityUpdateSystem;

    public EnergyRegenerationSystem(float interval) {
        super(Aspect.all(Stamina.class), interval);
    }

    @Override
    protected void process(E e) {
        Stamina stamina = e.getStamina();
        if (stamina.min < stamina.max) {
            int missingStamina = stamina.max - stamina.min;
            int recoveredStamina = stamina.max * REGENERATION_PERCENT / 100;
            stamina.min = Math.min(stamina.min + recoveredStamina, stamina.max);

            // notify user
            EntityUpdate update = EntityUpdateBuilder.of(e.id()).withComponents(stamina).build();
            entityUpdateSystem.add(update, UpdateTo.ENTITY);
        }
    }
}
