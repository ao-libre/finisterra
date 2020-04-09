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
import game.systems.network.ClientSystem;
import shared.util.LogSystem;

/**
 * Esta es la <b>clase principal</b> de la aplicación.
 * Ver la documentación de libGDX sobre {@link ApplicationListener}
 * para detalles del funcionamiento interno.
 */
public class AOGame extends Game implements AssetManagerHolder {

    private final AOAssetManager assetManager;
    private final ClientConfiguration clientConfiguration;
    private final ClientSystem clientSystem;
    private final ScreenManager screenManager;
    private World world;
    private Sync fpsSync;

    /**
     * Constructor de la clase.
     * Acá no hay contexto de libGDX, ver {@link AOGame#create()}
     */
    public AOGame(ClientConfiguration clientConfiguration) {
        Log.setLogger(new LogSystem());
        this.clientConfiguration = clientConfiguration;
        this.assetManager = new DefaultAOAssetManager(clientConfiguration);
        this.clientSystem = new ClientSystem();
        this.screenManager = new ScreenManager(this);

        // @todo construir world de forma asincrónica y mostrar pantalla de carga
        this.world = new WorldConstructor(assetManager, clientConfiguration, clientSystem, screenManager).build();
    }

    // Crea la ventana del juego.
    @Override
    public void create() {
        Log.setLogger(new LogSystem());
        Log.debug("AOGame", "Creating AOGame...");
        screenManager.to(ScreenEnum.LOADING);
        this.fpsSync = new Sync();
        // @todo load platform-independent configuration (network, etc.)
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

    /*
        Este metodo te permite acceder a el objeto que administra los recursos del juego
        desde CUALQUIER parte del proyecto.
    */
    public static AOAssetManager getGlobalAssetManager() {
        AssetManagerHolder game = (AssetManagerHolder) Gdx.app.getApplicationListener();
        return game.getAssetManager();
    }

    @Override
    public AOAssetManager getAssetManager() {
        return assetManager;
    }

    public ClientConfiguration getClientConfiguration() {
        return clientConfiguration;
    }

    public ClientSystem getClientSystem() { // @todo inyectar ClientSystem en consumidores
        return clientSystem;
    }

    public World getWorld() { return world; }

    public void toGame(GameScreen gameScreen) {
        setScreen(gameScreen);
    }
}
