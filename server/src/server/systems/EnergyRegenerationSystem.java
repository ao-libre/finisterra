package server.systems;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import component.entity.character.status.Stamina;
import server.systems.network.EntityUpdateSystem;
import server.systems.network.UpdateTo;
import shared.network.notifications.EntityUpdate;
import shared.util.EntityUpdateBuilder;

@Wire
public class EnergyRegenerationSystem extends IntervalFluidIteratingSystem {

    private EntityUpdateSystem entityUpdateSystem;

    public static final int REGENERATION_PERCENT = 10;

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
