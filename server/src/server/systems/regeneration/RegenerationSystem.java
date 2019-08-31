package server.systems.regeneration;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.E;
import entity.character.status.*;
import server.systems.IntervalFluidIteratingSystem;
import server.systems.manager.WorldManager;
import shared.network.notifications.EntityUpdate;
import shared.network.notifications.EntityUpdate.EntityUpdateBuilder;

public class RegenerationSystem extends IntervalFluidIteratingSystem {

    public RegenerationSystem(float interval) {
        super(Aspect.one(Stamina.class, Health.class, Mana.class), interval);
    }

    @Override
    protected void process(E e) {
        // TODO extract percent to component
        float percent = e.hasRegeneration() ? e.getRegeneration().getMultiplier() : Regeneration.DEFAULT;
        recoverStat(e, e.getHealth(), e.hasStrength() ? e.getStrength().getCurrentValue() : 15 * percent / 100f);
        recoverStat(e, e.getMana(), e.hasIntelligence() ? e.getIntelligence().getCurrentValue() : 15 * percent  / 10f);
        recoverStat(e, e.getStamina(), e.hasAgility() ? e.getAgility().getCurrentValue() : 15 * percent / 10f);
    }

    private void recoverStat(E e, Stat stat, float percent) {
        if (stat == null) {
            return;
        }
        if (stat.getMin() < stat.getMax()) {
            int recoveredStat = (int) (stat.getMax() * percent / 100);
            stat.setMin(Math.min(stat.getMin() + recoveredStat, stat.getMax()));

            if (recoveredStat > 0) {
                // notify user
                EntityUpdate update = EntityUpdateBuilder.of(e.id()).withComponents((Component) stat).build();
                WorldManager worldManager = world.getSystem(WorldManager.class);
                worldManager.sendEntityUpdate(e.id(), update);
            }
        }
    }
}
