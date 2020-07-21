package server.systems.world.entity.user;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.FluidIteratingSystem;
import com.artemis.annotations.Wire;
import component.entity.character.states.Buff;
import server.systems.network.EntityUpdateSystem;
import server.systems.world.WorldEntitiesSystem;
import server.utils.UpdateTo;
import shared.util.EntityUpdateBuilder;

@Wire
public class BuffSystem extends FluidIteratingSystem {

    private WorldEntitiesSystem worldEntitiesSystem;
    private EntityUpdateSystem entityUpdateSystem;

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
                entityUpdateSystem.add(update.build(), UpdateTo.ENTITY);
            }
        });

    }
}
