package design.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class ScreenManager {

    private static ScreenManager instance;

    private Game game;
    private Screen current;

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
        current = newScreen;
    }

    public void showScreen(Screen screen) {
        game.setScreen(screen);
        current = screen;
    }

    public Screen getCurrent() {
        return current;
    }
}
