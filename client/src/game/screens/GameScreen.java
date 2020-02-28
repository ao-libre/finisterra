package game.screens;

import com.artemis.Entity;
import com.artemis.SuperMapper;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.managers.TagManager;
import com.artemis.managers.UuidEntityManager;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureArraySpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import game.AOGame;
import game.ClientConfiguration;
import game.handlers.*;
import game.managers.MapManager;
import game.managers.WorldManager;
import game.network.ClientResponseProcessor;
import game.network.GameNotificationProcessor;
import game.network.KryonetClientMarshalStrategy;
import game.systems.anim.IdleAnimationSystem;
import game.systems.anim.MovementAnimationSystem;
import game.systems.camera.CameraFocusSystem;
import game.systems.camera.CameraMovementSystem;
import game.systems.camera.CameraShakeSystem;
import game.systems.camera.CameraSystem;
import game.systems.map.TiledMapSystem;
import game.systems.network.ClientSystem;
import game.systems.network.TimeSync;
import game.systems.physics.MovementProcessorSystem;
import game.systems.physics.MovementSystem;
import game.systems.physics.PhysicsAttackSystem;
import game.systems.physics.PlayerInputSystem;
import game.systems.render.ui.CoordinatesRenderingSystem;
import game.systems.render.world.*;
import game.systems.sound.SoundSytem;
import game.ui.GUI;
import shared.model.map.Tile;

import java.util.concurrent.TimeUnit;

import static com.artemis.E.E;
import static com.artemis.WorldConfigurationBuilder.Priority.HIGH;
import com.esotericsoftware.minlog.Log;

public class GameScreen extends ScreenAdapter implements WorldScreen {

    private static final int HANDLER_PRIORITY = WorldConfigurationBuilder.Priority.NORMAL + 3;
    private static final int ENTITY_RENDER_PRIORITY = WorldConfigurationBuilder.Priority.NORMAL + 2;
    private static final int PRE_ENTITY_RENDER_PRIORITY = ENTITY_RENDER_PRIORITY + 1;
    private static final int POST_ENTITY_RENDER_PRIORITY = ENTITY_RENDER_PRIORITY - 1;
    private static final int DECORATION_PRIORITY = ENTITY_RENDER_PRIORITY - 2;
    private static final int GUI = DECORATION_PRIORITY - 1;

    public static World world;
    public static int player = -1;
    private final ClientConfiguration clientConfiguration;
    private final FPSLogger logger;
    private final Batch spriteBatch;
    private WorldConfigurationBuilder worldConfigBuilder;
    private final AOAssetManager assetManager;
    private final Music backgroundMusic = MusicHandler.BACKGROUNDMUSIC;

    public GameScreen(ClientConfiguration clientConfiguration, AOAssetManager assetManager) {

        this.clientConfiguration = clientConfiguration;
        this.assetManager = assetManager;
        this.spriteBatch = initBatch();
        this.logger = new FPSLogger();
        long start = System.currentTimeMillis();
        initWorldConfiguration();
        Log.info("Game screen initialization", "Elapsed time: " + TimeUnit.MILLISECONDS.toSeconds(Math.abs(System.currentTimeMillis() - start)));
    }

    public static int getPlayer() {
        return player;
    }

    public static void setPlayer(int player) {
        GameScreen.player = player;
        world.getSystem(GUI.class).getInventory().updateUserInventory(0);
        world.getSystem(GUI.class).getSpellView().updateSpells();
        world.getSystem(GUI.class).getSpellViewExpanded ().updateSpells();

    }

    public static KryonetClientMarshalStrategy getClient() {
        return world.getSystem(ClientSystem.class).getKryonetClient();
    }

    @Override
    public World getWorld() {
        return world;
    }

    private void initWorldConfiguration() {
        worldConfigBuilder = new WorldConfigurationBuilder();
        worldConfigBuilder.with(new SuperMapper())
                .with(HIGH, new TimeSync())
                // Player movement
                .with(HIGH, new PlayerInputSystem())
                .with(HIGH, new MovementProcessorSystem())
                .with(HIGH, new MovementAnimationSystem())
                .with(HIGH, new IdleAnimationSystem())
                .with(HIGH, new MovementSystem())
                // Camera
                .with(HIGH, new CameraSystem(AOGame.GAME_SCREEN_ZOOM))
                .with(HIGH, new CameraFocusSystem())
                .with(HIGH, new CameraMovementSystem())
                .with(HIGH, new CameraShakeSystem())
                // Logic systems
                .with(HIGH, new WorldManager())
                .with(HIGH, new PhysicsAttackSystem())
                // Sound systems
                .with(HIGH, new SoundSytem())
                .with(HIGH, new TiledMapSystem())
                // Handlers
                .with(HANDLER_PRIORITY, new AnimationHandler(assetManager))
                .with(HANDLER_PRIORITY, new DescriptorHandler(assetManager))
                .with(HANDLER_PRIORITY, new MapHandler())
                .with(HANDLER_PRIORITY, new MusicHandler())
                .with(HANDLER_PRIORITY, new ObjectHandler())
                .with(HANDLER_PRIORITY, new ParticlesHandler())
                .with(HANDLER_PRIORITY, new SoundsHandler())
                .with(HANDLER_PRIORITY, new SpellHandler())
                .with(HANDLER_PRIORITY, new FontsHandler())
                // Rendering
                .with(PRE_ENTITY_RENDER_PRIORITY, new MapGroundRenderingSystem(spriteBatch))
                .with(PRE_ENTITY_RENDER_PRIORITY, new ObjectRenderingSystem(spriteBatch))
                .with(PRE_ENTITY_RENDER_PRIORITY, new TargetRenderingSystem(spriteBatch))
                .with(PRE_ENTITY_RENDER_PRIORITY, new NameRenderingSystem(spriteBatch))
                .with(ENTITY_RENDER_PRIORITY, new EffectRenderingSystem(spriteBatch))
                .with(ENTITY_RENDER_PRIORITY, new CharacterRenderingSystem(spriteBatch))
                .with(ENTITY_RENDER_PRIORITY, new WorldRenderingSystem(spriteBatch))
                .with(POST_ENTITY_RENDER_PRIORITY, new CombatRenderingSystem(spriteBatch))
                .with(POST_ENTITY_RENDER_PRIORITY, new DialogRenderingSystem(spriteBatch))
                .with(POST_ENTITY_RENDER_PRIORITY, new MapLastLayerRenderingSystem(spriteBatch))
                .with(POST_ENTITY_RENDER_PRIORITY, new LightRenderingSystem(spriteBatch))
                .with(DECORATION_PRIORITY, new StateRenderingSystem(spriteBatch))
                .with(DECORATION_PRIORITY, new CharacterStatesRenderingSystem(spriteBatch))
                .with(WorldConfigurationBuilder.Priority.NORMAL, new CoordinatesRenderingSystem(spriteBatch))
                .with(WorldConfigurationBuilder.Priority.NORMAL, new BuffRenderingSystem(spriteBatch))
                // GUI
                .with(GUI, new GUI())
                // Other
                .with(new MapManager())
                .with(new TagManager())
                .with(new UuidEntityManager())
                .with(clientConfiguration);

    }

    public void initWorld(ClientSystem clientSystem) {
        worldConfigBuilder
                .with(HIGH + 1, new ClientResponseProcessor())
                .with(HIGH + 1, new GameNotificationProcessor())
                .with(HIGH + 1, clientSystem);
        world = new World(worldConfigBuilder.build()); // preload Artemis world
    }

    public static Batch initBatch() {
        Batch tempSpriteBatch;
        try {
            tempSpriteBatch = new TextureArraySpriteBatch();
        } catch (Exception ex) {
            Log.info("Tu dispositivo no es compatible con el SpriteBatch mejorado. Usando sistema original...");
            tempSpriteBatch = new SpriteBatch();
        }
        return tempSpriteBatch;
    }

    private void postWorldInit() {
        Entity cameraEntity = world.createEntity();
        E(cameraEntity)
                .aOCamera(true)
                .pos2D();

        // for testing
        backgroundMusic.setVolume ( 0.20f );
        backgroundMusic.play ();
    }

    protected void update(float deltaTime) {
        this.logger.log();

        world.setDelta(MathUtils.clamp(deltaTime, 0, 1 / 14f));
        world.process();
    }

    public OrthographicCamera getGUICamera() {
        return world.getSystem(CameraSystem.class).guiCamera;
    }

    @Override
    public void show() {
        this.postWorldInit();
    }

    @Override
    public void render(float delta) {
        this.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        CameraSystem cameraSystem = world.getSystem(CameraSystem.class);
        cameraSystem.camera.viewportWidth = Tile.TILE_PIXEL_WIDTH * 24f;  //We will see width/32f units!
        cameraSystem.camera.viewportHeight = cameraSystem.camera.viewportWidth * height / width;
        cameraSystem.camera.update();

        getWorld().getSystem(GUI.class).getStage().getViewport().update(width, height);

        getWorld().getSystem(LightRenderingSystem.class).resize(width, height);
    }

    @Override
    public void dispose() {
        world.getSystem(ClientSystem.class).stop();
        world.getSystem(GUI.class).dispose();
        backgroundMusic.stop ();
    }

}
