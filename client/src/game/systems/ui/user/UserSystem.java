package game.systems.ui.user;

import com.artemis.Aspect;
import com.badlogic.gdx.scenes.scene2d.Actor;
import entity.character.status.Health;
import game.systems.ui.UserInterfaceContributionSystem;
import game.ui.user.UserInformation;

public class UserSystem extends UserInterfaceContributionSystem {

    public UserSystem() {
        super(Aspect.all(Health.class));
    }

    @Override
    protected void calculate(int entityId) {

    }

    @Override
    public Actor getActor() {
        return new UserInformation();
    }
}
