package game;

import com.artemis.BaseSystem;
import com.artemis.World;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.esotericsoftware.minlog.Log;
import game.screens.LoadingScreen;
import game.screens.ScreenEnum;
import game.screens.ScreenManager;
import shared.util.LogSystem;

/**
 * Esta es la <b>clase principal</b> de la aplicación.
 * Ver la documentación de libGDX sobre {@link ApplicationListener}
 * para detalles del funcionamiento interno.
 */
public class AOGame extends Game {

    private AssetManager assetManager;
    private ClientConfiguration clientConfiguration;
    private World world;

    /**
     * Constructor de la clase.
     * Acá no hay contexto de libGDX, ver {@link AOGame#create()}
     */
    public AOGame(ClientConfiguration clientConfiguration) {
        Log.setLogger(new LogSystem());
        this.clientConfiguration = clientConfiguration;
    }

    // Crea la ventana del juego.
    @Override
    public void create() {
        Log.debug("AOGame", "Creating AOGame...");
        // Create Loading screen
        LoadingScreen screen = new LoadingScreen(clientConfiguration);
        setScreen(screen);
        screen.onFinished((assetManager) -> {
            ScreenManager screenManager = new ScreenManager(this);
            this.assetManager = assetManager;
            this.world = WorldConstructor.create(clientConfiguration, screenManager, assetManager);
            screenManager.to(ScreenEnum.LOGIN);
            screenManager.addListener((screenEnum -> {
                if (screenEnum.equals(ScreenEnum.LOGIN)) {
                    this.world = WorldConstructor.create(clientConfiguration, screenManager, assetManager);
                }
            }));
        });
    }

    /**
     * Disponer de todos los recursos utilizados y cerrar threads pendientes.
     * Eventualmente la JVM cierra sola.
     */
    @Override
    public void dispose() {
        Log.debug("AOGame", "Closing client...");
        world.dispose(); /** Llama a {@link BaseSystem#dispose()} en todos los sistemas */
        assetManager.dispose(); // Libera todos los assets cargados
        Log.debug("AOGame", "Thank you for playing! See you soon...");
    }
}
