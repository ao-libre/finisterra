package game.systems.render;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;

@Wire
public class BatchEndSystem extends BaseSystem {

    private BatchSystem batchSystem;

    @Override
    protected void processSystem() {
        batchSystem.getBatch().end();
    }
}
