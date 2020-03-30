package game.systems.ui.dialog;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import game.systems.PlayerSystem;
import game.systems.network.ClientSystem;
import game.ui.DialogText;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import shared.network.interaction.TalkRequest;

@Wire
public class DialogSystem extends PassiveSystem {

    private ClientSystem clientSystem;
    private PlayerSystem playerSystem;

    public void talk() {
        if (isVisible()) {
            clientSystem.send(new TalkRequest(getMessage()));
        }
        playerSystem.get().writing(toggle());
    }

    private String getMessage() {
        return ""; // TODO
    }

    private boolean isVisible() {
        return false; // TODO
    }

    private boolean toggle() {
        // TODO
        return isVisible();
    }

    public Actor getActor() {
        // TODO
        DialogText dialog = new DialogText();
        float width = getWidth() * 0.8f;
        dialog.setSize(width, dialog.getHeight());
        dialog.setPosition((getWidth() - width) / 2, getHeight() / 2);
        return dialog;
    }

    private float getWidth() {
        return Gdx.graphics.getWidth();
    }

    private float getHeight() {
        return Gdx.graphics.getHeight();
    }
}
