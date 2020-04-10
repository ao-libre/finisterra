package game.systems.ui.action_bar.systems;

import com.artemis.Aspect;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.scenes.scene2d.Actor;
import component.entity.character.info.SpellBook;
import game.systems.resources.SpellsSystem;
import game.systems.screen.MouseSystem;
import game.systems.ui.UserInterfaceContributionSystem;
import game.systems.ui.action_bar.ActionBarSystem;
import game.ui.SpellBookUI;
import game.ui.SpellSlotUI;
import shared.model.Spell;

import static com.artemis.E.E;

@Wire
public class SpellSystem  extends UserInterfaceContributionSystem {

    private MouseSystem mouseSystem;
    private SpellsSystem spellsSystem;
    private ActionBarSystem actionBarSystem;
    private SpellBookUI spellView;

    public SpellSystem() {
        super(Aspect.all(SpellBook.class));
    }

    @Override
    public void calculate(int entityId) {
        spellView = new SpellBookUI() {
            @Override
            protected void onCastClicked(SpellSlotUI slot) {
                if (slot != null) {
                    mouseSystem.spell(slot.getSpell());
                }
            }

            @Override
            protected Spell getSpell(Integer spellId) {
                return spellsSystem.getSpell(spellId).orElse(null);
            }
        };
        spellView.update(E(entityId).getSpellBook(), 0);
        hide();
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

    public void clearCast() {
        actionBarSystem.clearCast();
    }

    public boolean isVisible() {
        return spellView.isVisible();
    }

    public void castClick(){
        spellView.castClick();
    }
}
