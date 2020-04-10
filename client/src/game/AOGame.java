package game;

import com.artemis.World;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.esotericsoftware.minlog.Log;
import game.handlers.AOAssetManager;
import game.handlers.DefaultAOAssetManager;
import game.screens.LoadingScreen;
import game.screens.ScreenEnum;
import game.screens.ScreenManager;
import game.systems.network.ClientSystem;
import shared.util.LogSystem;

/**
 * Esta es la <b>clase principal</b> de la aplicación.
 * Ver la documentación de libGDX sobre {@link ApplicationListener}
 * para detalles del funcionamiento interno.
 */
public class AOGame extends Game {

    private ClientConfiguration clientConfiguration;

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
            WorldConstructor.create(clientConfiguration, screenManager, assetManager);
            screenManager.to(ScreenEnum.LOGIN);
        });
    }

    /**
     * @todo disponer de todos los recursos utilizados y cerrar threads creados
     * Al final, la JVM debería cerrar sola.
     */
    @Override
    public void dispose() {
        Log.debug("AOGame", "Closing client...");
        screen.dispose();
        // @todo screenManager.dispose();
//        assetManager.dispose();
//        clientSystem.stop(); // @todo asegurarse que el thread de Kryonet cierre
        Log.debug("Thank you for playing! See you soon...");
        //System.exit(0);
    }

}
