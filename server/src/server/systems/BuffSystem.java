package server.systems;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.FluidIteratingSystem;
import com.artemis.annotations.Wire;
import entity.character.states.Buff;
import server.systems.manager.WorldManager;
import shared.network.notifications.EntityUpdate.EntityUpdateBuilder;

@Wire
public class BuffSystem extends FluidIteratingSystem {

    private WorldManager worldManager;

    public BuffSystem() {
        super(Aspect.all(Buff.class));
    }

    @Override
    protected void process(E e) {

        Buff buff = e.getBuff();

        float delta = getWorld().getDelta();

        buff.getBuffedAtributes().forEach((attribute, time) -> {

            buff.getBuffedAtributes().put(attribute, time -= delta);

            if (time <= 0) {
                attribute.resetCurrentValue();
                EntityUpdateBuilder update = EntityUpdateBuilder.of(e.id()).withComponents(attribute);
                buff.getBuffedAtributes().remove(attribute);
                if (buff.getBuffedAtributes().isEmpty()) {
                    e.removeBuff();
                    update.remove(Buff.class);
                } else {
                    update.withComponents(buff);
                }
                worldManager.sendEntityUpdate(e.id(), update.build());
            }
        });

    }
}
