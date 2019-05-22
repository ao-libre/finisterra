package server.systems;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.FluidIteratingSystem;
import com.artemis.systems.IteratingSystem;
import entity.character.attributes.Attribute;
import entity.character.states.Buff;

public class BuffSystem extends FluidIteratingSystem {

    public BuffSystem() {
        super(Aspect.all(Buff.class));;
    }

    @Override
    protected void process(E e) {

        Buff buff = e.getBuff();

        float delta = getWorld().getDelta();

        buff.setTime(buff.getTime() - delta);

        if (buff.getTime() <= 0)
        {
           Attribute attribute = buff.getAttribute();
            attribute.setCurrentValue(attribute.getBaseValue());
        }
    }
}
