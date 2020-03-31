package server.systems;

import com.artemis.Aspect;
import com.artemis.E;
import entity.character.status.Stamina;
import server.systems.manager.WorldManager;
import shared.network.notifications.EntityUpdate;
import shared.util.EntityUpdateBuilder;

public class EnergyRegenerationSystem extends IntervalFluidIteratingSystem {

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
            WorldManager worldManager = world.getSystem(WorldManager.class);
            worldManager.sendEntityUpdate(e.id(), update);
        }
    }
}
