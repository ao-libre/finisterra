package game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.esotericsoftware.minlog.Log;
import game.handlers.AOAssetManager;
import game.handlers.DefaultAOAssetManager;
import game.screens.ScreenEnum;
import game.screens.ScreenManager;
import game.screens.transitions.ColorFadeTransition;
import game.screens.transitions.FadingGame;
import game.utils.Cursors;

/**
 * Represents the game application.
 * Implements {@link ApplicationListener}.
 * <p>
 * This should be the primary instance of the app.
 */
public class AOGame extends FadingGame {

    public static final float GAME_SCREEN_ZOOM = 1f;
    public static final float GAME_SCREEN_MAX_ZOOM = 1.3f;

    private AOAssetManager assetManager = new DefaultAOAssetManager();

    @Override
    public void create() {
        super.create();
        Gdx.app.debug("AOGame", "Creating AOGame...");
        setTransition(new ColorFadeTransition(Color.BLACK, Interpolation.exp10), 1.0f);
        Cursors.setCursor("hand");
        ScreenManager.getInstance().initialize(this);
        toLoading();
        // @todo load platform-independent configuration (network, etc.)

    }

    private void toLoading() {
        ScreenManager.getInstance().showScreen(ScreenEnum.LOADING);
    }

    public void toLogin() {
        ScreenManager.getInstance().showScreen(ScreenEnum.LOGIN);
    }

    public void toLobby(Object... params) {
        ScreenManager.getInstance().showScreen(ScreenEnum.LOBBY, params);
    }

    public void toRoom(Object... params) {
        ScreenManager.getInstance().showScreen(ScreenEnum.ROOM, params);
    }

    public AOAssetManager getAssetManager() {
        return assetManager;
    }

    public void dispose() {
        Log.info("Closing client...");
        screen.dispose();
        getAssetManager().dispose();
        Gdx.app.exit();
        Log.info("Thank you for playing! See you soon...");
        System.exit(0);
    }
}