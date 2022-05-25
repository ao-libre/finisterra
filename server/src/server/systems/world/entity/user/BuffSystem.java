package server.systems.world.entity.user;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import component.entity.character.states.Buff;
import server.systems.network.EntityUpdateSystem;
import server.systems.world.WorldEntitiesSystem;
import server.utils.UpdateTo;
import shared.util.EntityUpdateBuilder;

@Wire
public class BuffSystem extends IteratingSystem {

    private WorldEntitiesSystem worldEntitiesSystem;
    private EntityUpdateSystem entityUpdateSystem;

    ComponentMapper<Buff> mBuff;

    public BuffSystem() {
        super(Aspect.all(Buff.class));
    }

    @Override
    protected void process(int entityId) {

        Buff buff = mBuff.get(entityId);

        float delta = getWorld().getDelta();

        buff.getBuffedAtributes().forEach((attribute, time) -> {

            buff.getBuffedAtributes().put(attribute, time -= delta);

            if (time <= 0) {
                attribute.resetCurrentValue();
                EntityUpdateBuilder update = EntityUpdateBuilder.of(entityId).withComponents(attribute);
                buff.getBuffedAtributes().remove(attribute);
                if (buff.getBuffedAtributes().isEmpty()) {
                    mBuff.remove(entityId);
                    update.remove(Buff.class);
                } else {
                    update.withComponents(buff);
                }
                entityUpdateSystem.add(update.build(), UpdateTo.ENTITY);
            }
        });

    }
}
