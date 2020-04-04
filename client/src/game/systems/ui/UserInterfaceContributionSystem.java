package game.systems.ui;

import component.camera.Focused;
import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.badlogic.gdx.scenes.scene2d.Actor;

public abstract class UserInterfaceContributionSystem extends BaseEntitySystem {

    public UserInterfaceContributionSystem(Aspect.Builder aspect) {
        super(aspect.all(Focused.class));
    }

    @Override
    protected void inserted(int entityId) {
        super.inserted(entityId);
//        calculate(entityId);
    }

    protected abstract void calculate(int entityId);

    protected abstract Actor getActor();

    @Override
    protected void processSystem() {
        // do nothing
    }
}
