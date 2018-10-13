package ar.com.tamborindeguy.client.managers;

import ar.com.tamborindeguy.client.handlers.ParticlesHandler;
import ar.com.tamborindeguy.client.screens.GameScreen;
import ar.com.tamborindeguy.client.ui.GUI;
import ar.com.tamborindeguy.client.utils.Keys;
import ar.com.tamborindeguy.network.interaction.MeditateRequest;
import ar.com.tamborindeguy.network.interaction.TalkRequest;
import ar.com.tamborindeguy.network.notifications.EntityUpdate;
import com.artemis.Component;
import com.artemis.E;
import com.badlogic.gdx.scenes.scene2d.Stage;

import static com.artemis.E.E;

public class AOInputProcessor extends Stage {

    @Override
    public boolean keyUp(int keycode) {
        E player = E(GameScreen.getPlayer());
        if (!(player.isWriting() || player.isMoving())) {
            switch (keycode) {
                case Keys.INVENTORY:
                    toggleInventory();
                    break;
                case Keys.MEDITATE:
                    toggleMeditate();
                    break;
                case Keys.TALK:
                    toggleDialogText();
                    break;
            }
        }
        return super.keyUp(keycode);
    }

    private void toggleDialogText() {
        if (GUI.getDialog().isVisible()) {
            String message = GUI.getDialog().getMessage();
            GameScreen.getClient().sendToAll(new TalkRequest(message));
        }
        GUI.getDialog().toggle();
    }

    private void toggleMeditate() {
        GameScreen.getClient().sendToAll(new MeditateRequest());
    }

    private void toggleInventory() {
        GUI.getInventory().setVisible(!GUI.getInventory().isVisible());
    }

}
