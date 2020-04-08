package game.systems.ui.dialog;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import game.systems.PlayerSystem;
import game.systems.network.ClientSystem;
import game.ui.DialogText;
import game.utils.AlternativeKeys;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import shared.network.interaction.TalkRequest;

@Wire
public class DialogSystem extends PassiveSystem {

    private ClientSystem clientSystem;
    private PlayerSystem playerSystem;

    public DialogText dialogText;

    public DialogSystem() {
        dialogText = new DialogText();
        float width = getWidth() * 0.8f;
        dialogText.setSize(width, dialogText.getHeight());
        dialogText.setPosition((getWidth() - width) / 2, getHeight() / 2);
        dialogText.addListener(new InputListener(){
            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                if (keycode == AlternativeKeys.TALK) {
                    talk();
                }
                return keycode == AlternativeKeys.TALK;
            }
        });
    }

    public void talk() {
        if (dialogText.isVisible()) {
            clientSystem.send(new TalkRequest(getMessage()));
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
        return dialogText.isVisible();
    }

    public Actor getActor() {
        return dialogText;
    }

    private float getWidth() {
        return Gdx.graphics.getWidth();
    }

    private float getHeight() {
        return Gdx.graphics.getHeight();
    }
}
