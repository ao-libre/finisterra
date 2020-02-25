package game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.esotericsoftware.minlog.Log;
import shared.util.LogSystem;
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
public class  AOGame extends FadingGame implements AssetManagerHolder {

    public static final float GAME_SCREEN_ZOOM = 1f;
    public static final float GAME_SCREEN_MAX_ZOOM = 1.3f;

    private final AOAssetManager assetManager;
    private final ClientConfiguration clientConfiguration;

    public AOGame(ClientConfiguration clientConfiguration) {
        Log.setLogger(new LogSystem());
        this.clientConfiguration = clientConfiguration;
        this.assetManager = new DefaultAOAssetManager(clientConfiguration);
    }

    /*
        Este metodo te permite acceder a el objeto que administra los recursos del juego
        desde CUALQUIER parte del proyecto.
    */
    public static AOAssetManager getGlobalAssetManager() {
        AssetManagerHolder game = (AssetManagerHolder) Gdx.app.getApplicationListener();
        return game.getAssetManager();
    }

    // Crea la ventana del juego.
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

    // Muestra la pantalla de carga.
    private void toLoading() {
        ScreenManager.getInstance().showScreen(ScreenEnum.LOADING);
    }

    // Muestra la pantalla de inicio de sesion.
    public void toLogin() {
        ScreenManager.getInstance().showScreen(ScreenEnum.LOGIN);
    }

    // Muestra la pantalla del lobby.
    public void toLobby(Object... params) {
        ScreenManager.getInstance().showScreen(ScreenEnum.LOBBY, params);
    }

    // Muestra la pantalla de la sala de jugadores.
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

    @Override
    public AOAssetManager getAssetManager() {
        return assetManager;
    }

    @Override
    public void dispose() {
        Log.info("AOGame","Closing client...");
        screen.dispose();
        assetManager.dispose();
        Gdx.app.exit();
        Log.info("Thank you for playing! See you soon...");
        System.exit(0);
    }
}
