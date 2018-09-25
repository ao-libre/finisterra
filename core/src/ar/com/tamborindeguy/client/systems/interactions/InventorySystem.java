package ar.com.tamborindeguy.client.systems.interactions;

import camera.Focused;
import com.artemis.Aspect;
import com.artemis.systems.IteratingSystem;

public class InventorySystem extends IteratingSystem {
    public InventorySystem() {
        super(Aspect.all(Focused.class));
    }

    @Override
    protected void process(int entityId) {

    }
}
