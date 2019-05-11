package game.screens;

import com.artemis.Entity;
import com.artemis.SuperMapper;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.managers.TagManager;
import com.artemis.managers.UuidEntityManager;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import game.AOGame;
import game.handlers.MusicHandler;
import game.systems.Sound.SoundSytem;
import game.systems.anim.IdleAnimationSystem;
import game.systems.anim.MovementAnimationSystem;
import game.systems.camera.CameraFocusSystem;
import game.systems.camera.CameraMovementSystem;
import game.systems.camera.CameraSystem;
import game.systems.map.CaveSystem;
import game.systems.network.ClientSystem;
import game.systems.network.TimeSync;
import game.systems.physics.MovementProcessorSystem;
import game.systems.physics.MovementSystem;
import game.systems.physics.PhysicsAttackSystem;
import game.systems.physics.PlayerInputSystem;
import game.systems.render.ui.CoordinatesRenderingSystem;
import game.systems.render.world.*;
import game.ui.GUI;
import net.mostlyoriginal.api.network.marshal.common.MarshalStrategy;
import shared.model.lobby.Player;
import shared.model.map.Tile;
import shared.network.lobby.player.PlayerLoginRequest;

import static com.artemis.E.E;

public class GameScreen extends ScreenAdapter {

    private static final int FONTS_PRIORITY = WorldConfigurationBuilder.Priority.NORMAL - 1;
    public static World world;
    public static int player = -1;
    private static GUI gui = new GUI();
    private static ClientSystem clientSystem;
    protected FPSLogger logger;
    protected GameState state;
    private SpriteBatch spriteBatch;
    private CameraSystem cameraSystem;

    public GameScreen(String host, int port, Player player) {
        this.clientSystem = new ClientSystem(host, port);
        this.spriteBatch = new SpriteBatch();
        this.logger = new FPSLogger();
        clientSystem.start();
        initWorld();
        clientSystem.getKryonetClient().sendToAll(new PlayerLoginRequest(player));
    }

    public static int getPlayer() {
        return player;
    }

    public static void setPlayer(int player) {
        GameScreen.player = player;
        GUI.getInventory().updateUserInventory();
        GUI.getSpellView().updateSpells();
        GUI.getUserTable().refresh();
    }

    public static MarshalStrategy getClient() {
        return clientSystem.getKryonetClient();
    }

    public static GUI getGui() {
        return gui;
    }

    public static World getWorld() {
        return world;
    }

    public void initWorld() {
        WorldConfigurationBuilder worldConfigBuilder = new WorldConfigurationBuilder();
        cameraSystem = new CameraSystem(AOGame.GAME_SCREEN_ZOOM);
        worldConfigBuilder.with(new SuperMapper())
                .with(clientSystem)
                .with(new TimeSync())
                // Player movement
                .with(new PlayerInputSystem())
                .with(new MovementProcessorSystem())
                .with(new MovementAnimationSystem())
                .with(new IdleAnimationSystem())
                .with(new MovementSystem())
                // Camera
                .with(cameraSystem)
                .with(new CameraFocusSystem())
                .with(new CameraMovementSystem())
                // Logic systems
                .with(new PhysicsAttackSystem())
                // Sound systems
                .with(new SoundSytem())
                // Rendering
                .with(WorldConfigurationBuilder.Priority.NORMAL + 5, new CaveSystem())
                .with(WorldConfigurationBuilder.Priority.NORMAL + 3, new GroundFXsRenderingSystem(spriteBatch))
                .with(WorldConfigurationBuilder.Priority.NORMAL + 3, new TargetRenderingSystem(spriteBatch))
                .with(WorldConfigurationBuilder.Priority.NORMAL + 2, new CharacterRenderingSystem(spriteBatch))
                .with(WorldConfigurationBuilder.Priority.NORMAL + 3, new NameRenderingSystem(spriteBatch))
                .with(WorldConfigurationBuilder.Priority.NORMAL + 1, new ObjectRenderingSystem(spriteBatch))
                .with(WorldConfigurationBuilder.Priority.NORMAL + 1, new ParticleRenderingSystem(spriteBatch))
                .with(WorldConfigurationBuilder.Priority.NORMAL + 1, new FXsRenderingSystem(spriteBatch))
                .with(FONTS_PRIORITY, new StateRenderingSystem(spriteBatch))
                .with(FONTS_PRIORITY, new CombatRenderingSystem(spriteBatch))
                .with(FONTS_PRIORITY, new DialogRenderingSystem(spriteBatch))
                .with(FONTS_PRIORITY, new CharacterStatesRenderingSystem(spriteBatch))
                .with(FONTS_PRIORITY - 1, new LightRenderingSystem(spriteBatch))
                .with(WorldConfigurationBuilder.Priority.NORMAL, new CoordinatesRenderingSystem(spriteBatch))
                // Other
                .with(new TagManager())
                .with(new UuidEntityManager()); // why?

        world = new World(worldConfigBuilder.build()); // preload Artemis world
    }

    protected void postWorldInit() {
        Entity cameraEntity = world.createEntity();
        E(cameraEntity)
                .aOCamera(true)
                .pos2D();
        world.getSystem(TagManager.class).register("camera", cameraEntity);

        MusicHandler.FadeOutMusic(101, 0.03f);
        //MusicHandler.stopMusic(101);
        MusicHandler.playMIDI(1);
    }

    protected void update(float deltaTime) {
        this.logger.log();

        world.setDelta(MathUtils.clamp(deltaTime, 0, 1 / 16f));
        world.process();

        switch (state) {
            case RUNNING: {
                updateRunning(deltaTime);
                break;
            }
            case PAUSED: {
                updatePaused();
                break;
            }
        }
    }

    public OrthographicCamera getGUICamera() {
        return cameraSystem.guiCamera;
    }

    @Override
    public void show() {
        this.postWorldInit();
        gui.initialize(); // TODO: gui.init() perhaps should on constructor but it has methods that shall execute on screen.show()
        this.state = GameState.RUNNING;
    }

    @Override
    public void render(float delta) {
        this.update(delta);
        if (player >= 0) {
            this.drawUI();
        }
    }

    @Override
    public void pause() {
        if (this.state == GameState.RUNNING) {
            this.state = GameState.PAUSED;
            this.pauseSystems();
        }
    }

    @Override
    public void resume() {
        if (this.state == GameState.PAUSED) {
            this.state = GameState.RUNNING;
            this.resumeSystems();
        }
    }

    @Override
    public void resize(int width, int height) {
        cameraSystem.camera.viewportWidth = Tile.TILE_PIXEL_WIDTH * 24f;  //We will see width/32f units!
        cameraSystem.camera.viewportHeight = cameraSystem.camera.viewportWidth * height / width;
        cameraSystem.camera.update();

        GUI.getStage().getViewport().update(width, height);

        getWorld().getSystem(LightRenderingSystem.class).resize(width, height);
    }

    protected void drawUI() {
        gui.draw();
    }

    @Override
    public void dispose() {
        gui.dispose();
    }

    protected void updateRunning(float deltaTime) {
        //
    }

    protected void updatePaused() {
        //
    }

    protected void pauseSystems() {
        //
    }

    protected void resumeSystems() {
        //
    }
}
