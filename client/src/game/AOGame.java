package game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.esotericsoftware.minlog.Log;
import game.handlers.AOAssetManager;
import game.handlers.DefaultAOAssetManager;
import game.screens.GameScreen;
import game.screens.ScreenEnum;
import game.screens.ScreenManager;
import shared.util.LogSystem;

/**
 * Represents the game application.
 * Implements {@link ApplicationListener}.
 * This should be the primary instance of the app.
 */

public class AOGame extends Game implements AssetManagerHolder {

    public static final float GAME_SCREEN_ZOOM = 1f;
    public static final float GAME_SCREEN_MAX_ZOOM = 1.3f;

    private final AOAssetManager assetManager;
    private final ClientConfiguration clientConfiguration;
    private Sync fpsSync;

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
        Log.setLogger(new LogSystem());
        Log.debug("AOGame", "Creating AOGame...");
        ScreenManager.getInstance().initialize(this);
        toLoading();
        this.fpsSync = new Sync();
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
    public void toSignUp(Object... params) {
        ScreenManager.getInstance().showScreen(ScreenEnum.SIGNUP, params);
    }

    public void toLobby(Object... params) {
        ScreenManager.getInstance().showScreen(ScreenEnum.LOBBY, params);
    }

    // Muestra la pantalla de la sala de jugadores.
    public void toRoom(Object... params) {
        ScreenManager.getInstance().showScreen(ScreenEnum.ROOM, params);
    }

    public void toGame(GameScreen gameScreen) {
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
    public void render() {
//        fpsSync.sync(100);
        super.render();
    }

    @Override
    public void dispose() {
        Log.debug("AOGame", "Closing client...");
        screen.dispose();
        assetManager.dispose();
        Gdx.app.exit();
        Log.debug("Thank you for playing! See you soon...");
        System.exit(0);
    }
}
