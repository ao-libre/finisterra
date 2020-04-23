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
import game.systems.resources.MusicSystem;
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
    private MusicSystem musicSystem;

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
            this.musicSystem = world.getSystem( MusicSystem.class );
            musicSystem.playMusic(101, true);
            screenManager.addListener((screenEnum -> {
                switch( screenEnum ) {
                    case LOGIN:
                        this.musicSystem = world.getSystem( MusicSystem.class );
                        musicSystem.stopMusic();
                        this.world = WorldConstructor.create( clientConfiguration, screenManager, assetManager );
                        this.musicSystem = world.getSystem( MusicSystem.class );
                        musicSystem.playMusic(101, true);
                        break;
                    case GAME:
                        this.musicSystem = world.getSystem( MusicSystem.class );
                        musicSystem.playMusic(1, true);
                }
            }));
        });
    }

    /**
     * GDX llama a este método cuando la aplicación cierra.
     * @see ApplicationListener#dispose()
     *
     * Disponer de todos los recursos utilizados y cerrar threads pendientes.
     * Eventualmente la JVM cierra sola.
     */
    @Override
    public void dispose() {
        Log.debug("AOGame", "Closing client...");
        if (world != null) {
            world.dispose(); // Llama a dispose() en todos los sistemas
            world = null;
        }
        if (assetManager != null) {
            assetManager.dispose(); // Libera todos los assets cargados
            assetManager = null;
        }
        Log.debug("AOGame", "Thank you for playing! See you soon...");
    }
}
