package ar.com.tamborindeguy.client.managers;

import ar.com.tamborindeguy.client.screens.GameScreen;
import ar.com.tamborindeguy.client.utils.Keys;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class AOInputProcessor extends Stage {

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Keys.INVENTORY:
                GameScreen.inventory.setVisible(!GameScreen.inventory.isVisible());
                break;
        }
        return super.keyUp(keycode);
    }
}
