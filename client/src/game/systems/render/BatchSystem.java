package game.systems.render;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import net.mostlyoriginal.api.system.core.PassiveSystem;

@Wire
public class BatchSystem extends PassiveSystem {

    private SpriteBatch spriteBatch;

    public BatchSystem() {
        spriteBatch = new SpriteBatch(4096);

    }

    public SpriteBatch getBatch() {
        return spriteBatch;
    }
}
