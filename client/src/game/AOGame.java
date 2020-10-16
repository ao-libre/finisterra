package game;

import com.artemis.World;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.esotericsoftware.minlog.Log;
import game.handlers.DefaultAOAssetManager;
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

    private DefaultAOAssetManager assetManager;
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
        assetManager = DefaultAOAssetManager.getInstance();
        LoadingScreen screen = new LoadingScreen(assetManager);
        setScreen(screen);
        screen.onFinished((assetManager) -> {
            ScreenManager screenManager = new ScreenManager(this);
            this.world = WorldConstructor.create(clientConfiguration, screenManager, assetManager);
            screenManager.to(ScreenEnum.LOGIN);
            this.musicSystem = world.getSystem(MusicSystem.class);
            musicSystem.playMusic(101, true);
            screenManager.addListener((screenEnum -> {
                switch (screenEnum) {
                    case LOGIN:
                        this.musicSystem = world.getSystem(MusicSystem.class);
                        musicSystem.stopMusic();
                        this.world = WorldConstructor.create(clientConfiguration, screenManager, assetManager);
                        this.musicSystem = world.getSystem(MusicSystem.class);
                        musicSystem.playMusic(101, true);
                        break;
                    case GAME:
                        this.musicSystem = world.getSystem(MusicSystem.class);
                        musicSystem.playMusic(1, true);
                }
            }));
        });
    }

    /**
     * libGDX llama a este método cuando la aplicación cierra.
     *
     * @see ApplicationListener#dispose()
     *
     * <b>Nota:</b> Para cerrar la aplicación usar {@code Gdx.app.exit()}
     * <p>
     * Disponer de todos los recursos utilizados y cerrar threads pendientes.
     * Eventualmente la JVM cierra sola.
     */
    @Override
    public void dispose() {
        Log.debug("AOGame", "Closing client...");
        if (world != null) world.dispose(); // Llama a dispose() en todos los sistemas
        if (assetManager != null) assetManager.dispose(); // Libera todos los assets cargados
        super.dispose();
        Log.debug("AOGame", "Thank you for playing! See you soon...");
    }
}
