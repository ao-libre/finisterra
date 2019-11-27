package game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.esotericsoftware.minlog.Log;
import game.handlers.AOAssetManager;
import game.handlers.DefaultAOAssetManager;
import game.screens.GameScreen;
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
public class AOGame extends FadingGame implements AssetManagerHolder {

    public static final float GAME_SCREEN_ZOOM = 1f;
    public static final float GAME_SCREEN_MAX_ZOOM = 1.3f;

    private final AOAssetManager assetManager;
    private final ClientConfiguration clientConfiguration;

    public AOGame(ClientConfiguration clientConfiguration) {
        this.clientConfiguration = clientConfiguration;
        this.assetManager = new DefaultAOAssetManager(clientConfiguration);
    }

    public static AOAssetManager getGlobalAssetManager() {
        AssetManagerHolder game = (AssetManagerHolder) Gdx.app.getApplicationListener();
        return game.getAssetManager();
    }

    @Override
    public void create() {
        super.create();
        Log.info("AOGame", "Creating AOGame...");
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

    public void toGame(GameScreen gameScreen) {
        setTransition(new ColorFadeTransition(Color.BLACK, Interpolation.exp10), 0f);
        setScreen(gameScreen);
    }

    public ClientConfiguration getClientConfiguration() {
        return clientConfiguration;
    }

    public AOAssetManager getAssetManager() {
        return assetManager;
    }

    public void dispose() {
        Log.info("AOGame","Closing client...");
        screen.dispose();
        getAssetManager().dispose();
        Gdx.app.exit();
        Log.info("Thank you for playing! See you soon...");
        System.exit(0);
    }
}
