package server.systems.battle;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.FluidIteratingSystem;
import entity.character.Character;
import position.WorldPos;

public class SpotRegenerationSystem extends FluidIteratingSystem {


    public SpotRegenerationSystem() {
        super(Aspect.all(WorldPos.class, Character.class)); // Todo regenerate component
    }

    @Override
    protected void process(E e) {

    }
}
