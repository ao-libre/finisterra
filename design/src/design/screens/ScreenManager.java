package design.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import game.AOGame;

public class ScreenManager {

    private static ScreenManager instance;

    private Game game;

    private ScreenManager() {
    }

    public static ScreenManager getInstance() {
        if (instance == null) {
            instance = new ScreenManager();
        }
        return instance;
    }

    public void initialize(Game game) {
        this.game = game;
    }

    // Show in the game the screen which enum type is received
    public void showScreen(ScreenEnum screenEnum, Object... params) {
        // Show new screen
        Screen newScreen = screenEnum.getScreen(params);
        game.setScreen(newScreen);
    }

}
