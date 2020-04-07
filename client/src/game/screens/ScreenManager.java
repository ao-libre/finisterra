package game.screens;

import com.badlogic.gdx.Screen;
import game.AOGame;
import net.mostlyoriginal.api.system.core.PassiveSystem;

public class ScreenManager extends PassiveSystem {
    private AOGame game;

    public ScreenManager(AOGame game) {
        this.game = game;
    }

    // Show in the game the screen which enum type is received
    public void showScreen(ScreenEnum screenEnum, Object... params) {
        // Show new screen
        Screen newScreen = screenEnum.getScreen(params);
        game.setScreen(newScreen);
    }

    private void toLoading() {
        showScreen(ScreenEnum.LOADING);
    }

    public void toLogin() {
        showScreen(ScreenEnum.LOGIN);
    }

    public void toSignUp(Object... params) {
        showScreen(ScreenEnum.SIGNUP, params);
    }

    public void toLobby(Object... params) {
        showScreen(ScreenEnum.LOBBY, params);
    }

    public void toRoom(Object... params) {
        showScreen(ScreenEnum.ROOM, params);
    }

}
