package game;

import com.artemis.World;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.esotericsoftware.minlog.Log;
import game.handlers.AOAssetManager;
import game.handlers.DefaultAOAssetManager;
import game.screens.GameScreen;
import game.screens.ScreenEnum;
import game.screens.ScreenManager;
import game.systems.network.ClientResponseProcessor;
import game.systems.network.ClientSystem;
import game.systems.network.GameNotificationProcessor;
import shared.util.LogSystem;

/**
 * Represents the game application.
 * Implements {@link ApplicationListener}.
 * This should be the primary instance of the app.
 */

public class AOGame extends Game implements AssetManagerHolder {

    private final AOAssetManager assetManager;
    private final ClientConfiguration clientConfiguration;
    private final ClientSystem clientSystem;
    private World world;
    private Sync fpsSync;

    public AOGame(ClientConfiguration clientConfiguration) {
        Log.setLogger(new LogSystem());
        this.clientConfiguration = clientConfiguration;
        this.assetManager = new DefaultAOAssetManager(clientConfiguration);
        this.clientSystem = new ClientSystem();
        clientSystem.setNotificationProcessor(new GameNotificationProcessor());
        clientSystem.setResponseProcessor(new ClientResponseProcessor());
        this.world = new WorldConstructor(this, assetManager, clientSystem, clientConfiguration).getWorld();
    }

    /*
        Este metodo te permite acceder a el objeto que administra los recursos del juego
        desde CUALQUIER parte del proyecto.
    */
    public static AOAssetManager getGlobalAssetManager() {
        AssetManagerHolder game = (AssetManagerHolder) Gdx.app.getApplicationListener();
        return game.getAssetManager();
    }

    public ClientSystem getClientSystem() { // @todo inyectar ClientSystem en consumidores
        return clientSystem;
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

    public ClientConfiguration getClientConfiguration() {
        return clientConfiguration;
    }

    @Override
    public AOAssetManager getAssetManager() {
        return assetManager;
    }

    public World getWorld() { return world; }

    public void toGame(GameScreen gameScreen) {
        setScreen(gameScreen);
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
