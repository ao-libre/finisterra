package ar.com.tamborindeguy.client.game;

import ar.com.tamborindeguy.client.handlers.AssetHandler;
import ar.com.tamborindeguy.client.handlers.HandlerState;
import ar.com.tamborindeguy.client.managers.AOInputProcessor;
import ar.com.tamborindeguy.client.screens.GameScreen;
import ar.com.tamborindeguy.client.screens.LoginScreen;
import ar.com.tamborindeguy.client.systems.anim.IdleAnimationSystem;
import ar.com.tamborindeguy.client.systems.anim.MovementAnimationSystem;
import ar.com.tamborindeguy.client.systems.camera.CameraFocusSystem;
import ar.com.tamborindeguy.client.systems.camera.CameraMovementSystem;
import ar.com.tamborindeguy.client.systems.camera.CameraSystem;
import ar.com.tamborindeguy.client.systems.map.TiledMapSystem;
import ar.com.tamborindeguy.client.systems.network.ClientSystem;
import ar.com.tamborindeguy.client.systems.physics.MovementProcessorSystem;
import ar.com.tamborindeguy.client.systems.physics.MovementSystem;
import ar.com.tamborindeguy.client.systems.physics.PhysicsAttackSystem;
import ar.com.tamborindeguy.client.systems.physics.PlayerInputSystem;
import ar.com.tamborindeguy.client.systems.render.ui.CoordinatesRenderingSystem;
import ar.com.tamborindeguy.client.systems.render.world.*;
import com.artemis.SuperMapper;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.managers.TagManager;
import com.artemis.managers.UuidEntityManager;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Represents the game application.
 * Implements {@link ApplicationListener}.
 *
 * This should be the primary instance of the app.
 */
public class AOGame extends Game {

    // TODO: Class to handle resources?
    public static final String GAME_GRAPHICS_PATH = "data/graficos/";
    public static final String GAME_FXS_PATH = "data/fxs/";
    public static final String GAME_PARTICLES_PATH = "data/particles/";
    public static final String GAME_FONTS_PATH = "data/fonts/";
    public static final String GAME_MAPS_PATH = "data/mapas/";
    public static final String GAME_INIT_PATH = "data/init/";
    public static final String GAME_SHADERS_PATH = "data/shaders/";
    public static final String GAME_GRAPHICS_EXTENSION = ".png";
    public static final String GAME_SHADERS_LIGHT = "light.png";

    // TODO: This is for use in LwjglApplicationConfiguration and is platform-specific. Move to DesktopLauncher.
    public static final int GAME_SCREEN_WIDTH = 1280;
    public static final int GAME_SCREEN_HEIGHT = 768;
    public static final float GAME_SCREEN_ZOOM = 1.8f;
    public static final boolean GAME_FULL_SCREEN = false;
    public static final boolean GAME_VSYNC_ENABLED = true;

    protected SpriteBatch spriteBatch; // This is only used in GameScreen

    private GameScreen gameScreen; // Game main screen
    private ClientSystem clientSystem; // Marshal client system (Kryonet)
    private World world;

    @Override
    public void create() {
        Gdx.app.debug("AOGame", "Creating AOGame...");
        // Loading screen
        //
        // Load resources & stuff.
        AssetHandler.load();
        if(AssetHandler.getState() == HandlerState.LOADED)
            Gdx.app.debug("AOGame", "Handler loaded!");
        // Initialize network stuff
        clientSystem = new ClientSystem();
        // TODO: Move this to login screen, read from text field, etc.
        clientSystem.getKryonetClient().setHost("ec2-18-219-97-32.us-east-2.compute.amazonaws.com");
        clientSystem.getKryonetClient().setPort(7666);
        //
        //
        this.spriteBatch = new SpriteBatch();
        // Artemis?
        // TODO: Preload Artemis world, etc.
        initWorld();
        //
        // Preload GameScreen
        gameScreen = new GameScreen(world);
        // Login screen / Launcher
        setScreen(new LoginScreen());
    }

    // TODO: Hotfix. Not the place
    private static final int FONTS_PRIORITY = WorldConfigurationBuilder.Priority.NORMAL - 1;

    public void initWorld() {
        WorldConfigurationBuilder worldConfigBuilder = new WorldConfigurationBuilder();
        worldConfigBuilder.with(new SuperMapper())
                .with(clientSystem)
                // Player movement
                .with(new PlayerInputSystem())
                .with(new MovementProcessorSystem())
                .with(new MovementAnimationSystem())
                .with(new IdleAnimationSystem())
                .with(new MovementSystem())
                // Camera
                .with(new CameraSystem(AOGame.GAME_SCREEN_ZOOM))
                .with(new CameraFocusSystem())
                .with(new CameraMovementSystem())
                // Logic systems
                .with(new PhysicsAttackSystem())
                // Rendering
                .with(WorldConfigurationBuilder.Priority.NORMAL + 5, new TiledMapSystem())
                .with(WorldConfigurationBuilder.Priority.NORMAL + 4, new MapLowerLayerRenderingSystem(spriteBatch))
                .with(WorldConfigurationBuilder.Priority.NORMAL + 3, new GroundFXsRenderingSystem(spriteBatch))
                .with(WorldConfigurationBuilder.Priority.NORMAL + 3, new ObjectRenderingSystem(spriteBatch))
                .with(WorldConfigurationBuilder.Priority.NORMAL + 3, new ParticleRenderingSystem(spriteBatch))
                .with(WorldConfigurationBuilder.Priority.NORMAL + 2, new CharacterRenderingSystem(spriteBatch))
                .with(WorldConfigurationBuilder.Priority.NORMAL + 1, new FXsRenderingSystem(spriteBatch))
                .with(WorldConfigurationBuilder.Priority.NORMAL + 1, new MapUpperLayerRenderingSystem(spriteBatch))
                .with(WorldConfigurationBuilder.Priority.NORMAL, new CoordinatesRenderingSystem(spriteBatch))
                .with(FONTS_PRIORITY, new StateRenderingSystem(spriteBatch))
                .with(FONTS_PRIORITY, new CombatRenderingSystem(spriteBatch))
                .with(WorldConfigurationBuilder.Priority.NORMAL + 3, new NameRenderingSystem(spriteBatch))
                .with(FONTS_PRIORITY, new DialogRenderingSystem(spriteBatch))
                .with(FONTS_PRIORITY, new CharacterStatesRenderingSystem(spriteBatch))
                // Other
                .with(new TagManager())
                .with(new UuidEntityManager()); // why?

        world = new World(worldConfigBuilder.build()); // preload Artemis world
    }

    @Override
    public void render() {
        GL20 gl = Gdx.gl;
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.render();
    }



    public void showGameScreen() {
        setScreen(gameScreen);
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }

    public void setGameScreen(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    public Screen getCurrentScreen() {
        return getScreen();
    }

    public ClientSystem getClientSystem() {
        return clientSystem;
    }
}
