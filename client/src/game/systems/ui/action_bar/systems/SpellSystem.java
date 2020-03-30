package game.systems.ui.action_bar.systems;

import com.artemis.Aspect;
import com.badlogic.gdx.scenes.scene2d.Actor;
import entity.character.info.SpellBook;
import game.systems.ui.UserInterfaceContributionSystem;

public class SpellSystem  extends UserInterfaceContributionSystem {

    public SpellSystem() {
        super(Aspect.all(SpellBook.class));
    }

    @Override
    protected void calculate(int entityId) {

    }

    @Override
    protected Actor getActor() {
        return null;
    }

    public void hide() {
        // TODO
    }

    public void show() {
        // TODO
    }

    public boolean isVisible() {
        return false; // TODO
    }
}
