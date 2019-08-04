package design.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import design.screens.map.MapEditor;
import design.screens.views.View;

public class ScreenManager {

    private static ScreenManager instance;

    private MapEditor mapEditorScreen;
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
        Screen current = game.getScreen();
        if (current != null) {
            if (current.getClass().equals(screenEnum.getType()) && params.length == 0) {
                ((View) current).clearListener();
                return;
            }
            current.pause();
        }

        Screen newScreen = screenEnum.getScreen(params);
        newScreen.resume();
        game.setScreen(newScreen);
        this.current = newScreen;
    }

    public void showScreen(Screen screen) {
        game.setScreen(screen);
        current = screen;
    }

    public void toMapEditorScreen() {
        if (mapEditorScreen == null) {
            mapEditorScreen = new MapEditor();
        }
        showScreen(mapEditorScreen);
    }

    public Screen getCurrent() {
        return current;
    }
}
