package game.systems.ui.dialog;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import game.systems.PlayerSystem;
import game.systems.network.ClientSystem;
import game.ui.DialogText;
import game.ui.WidgetFactory;
import game.utils.AlternativeKeys;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import shared.network.interaction.TalkRequest;

@Wire
public class DialogSystem extends PassiveSystem {

    public DialogText dialogText;
    private ClientSystem clientSystem;
    private PlayerSystem playerSystem;
    private Table table;

    public DialogSystem() {
        table = new Table();
        table.setVisible(false);
        table.setFillParent(true);
        dialogText = new DialogText();
        float width = getWidth() * 0.5f;
        dialogText.addListener(new InputListener() {
            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                if (keycode == AlternativeKeys.TALK) {
                    talk();
                }
                return keycode == AlternativeKeys.TALK;
            }
        });

        table.add(dialogText).expandY().bottom().width(width);
        ImageButton submitButton = WidgetFactory.createImageButton(WidgetFactory.ImageButtons.SUBMIT);
        submitButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                talk();
            }
        });
        table.add(submitButton).expandY().bottom().padLeft(-50);
    }

    public void talk() {
        if (dialogText.isVisible()) {
            clientSystem.send(new TalkRequest(getMessage().trim()));
            dialogText.getStage().setKeyboardFocus(null);
        } else {
            dialogText.getStage().setKeyboardFocus(dialogText);
        }
        playerSystem.get().writing(toggle());
    }

    private String getMessage() {
        return dialogText.getMessage();
    }

    private boolean toggle() {
        dialogText.toggle();
        table.setVisible(!table.isVisible());
        return dialogText.isVisible();
    }

    public Actor getActor() {
        return table;
    }

    private float getWidth() {
        return Gdx.graphics.getWidth();
    }

    private float getHeight() {
        return Gdx.graphics.getHeight();
    }
}
