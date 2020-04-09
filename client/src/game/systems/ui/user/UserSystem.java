package game.systems.ui.user;

import com.artemis.Aspect;
import com.badlogic.gdx.scenes.scene2d.Actor;
import component.entity.character.status.Health;
import game.systems.ui.UserInterfaceContributionSystem;
import game.ui.user.UserInformation;

import static com.artemis.E.E;

public class UserSystem extends UserInterfaceContributionSystem {

    private UserInformation actor;

    public UserSystem() {
        super(Aspect.all(Health.class));
    }

    @Override
    public void calculate(int entityId) {
        actor = new UserInformation(E(entityId));
    }

    @Override
    public Actor getActor() {
        return actor;
    }
}
