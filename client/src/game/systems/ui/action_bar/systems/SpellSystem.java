package game.systems.ui.action_bar.systems;

import com.artemis.Aspect;
import com.badlogic.gdx.scenes.scene2d.Actor;
import entity.character.info.SpellBook;
import game.systems.ui.UserInterfaceContributionSystem;
import game.ui.SpellView;

public class SpellSystem  extends UserInterfaceContributionSystem {

    private Actor spellView;

    public SpellSystem() {
        super(Aspect.all(SpellBook.class));
    }

    @Override
    public void calculate(int entityId) {
        spellView = new SpellView();
    }

    @Override
    public Actor getActor() {

        return spellView;
    }

    public void hide() {
        spellView.setVisible(false);
    }

    public void show() {
        spellView.setVisible(true);
    }

    public boolean isVisible() {
        return spellView.isVisible();
    }
}
