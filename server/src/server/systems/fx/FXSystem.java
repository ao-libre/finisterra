package server.systems.fx;

import com.artemis.annotations.Wire;
import graphics.Effect;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import server.systems.manager.WorldManager;
import shared.network.notifications.EntityUpdate.EntityUpdateBuilder;

import static com.artemis.E.E;

@Wire
public class FXSystem extends PassiveSystem {

    private WorldManager worldManager;

    public int attachFX(int id, int fx) {
        return attachFX(id, fx, -1);
    }

    public int attachFX(int id, int fx, int loops) {
        Effect effect = new Effect.EffectBuilder().attachTo(id).withFX(fx).withLoops(loops).build();
        return notifyEffect(id, effect, true);
    }

    public int attachParticle(int id, int particle, boolean front) {
        Effect effect = new Effect.EffectBuilder().attachTo(id).withParticle(particle).build();
        return notifyEffect(id, effect, front);
    }

    public int notifyEffect(int id, Effect effect, boolean front) {
        int fxE = world.create();
        EntityUpdateBuilder fxUpdate = EntityUpdateBuilder.of(fxE).withComponents(effect);
        if (!front) {
            E(fxE).renderBefore();
            fxUpdate.withComponents(E(fxE).getRenderBefore());
        }
        worldManager.notifyUpdate(id, fxUpdate.build());
        return fxE;
    }
}
