package game.screens;

import com.artemis.*;
import com.artemis.managers.TagManager;
import com.artemis.managers.UuidEntityManager;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import game.AOGame;
import game.systems.anim.IdleAnimationSystem;
import game.systems.anim.MovementAnimationSystem;
import game.systems.camera.CameraFocusSystem;
import game.systems.camera.CameraMovementSystem;
import game.systems.camera.CameraSystem;
import game.systems.map.CaveSystem;
import game.systems.map.MapSystem;
import game.systems.network.ClientSystem;
import game.systems.physics.MovementProcessorSystem;
import game.systems.physics.MovementSystem;
import game.systems.physics.PhysicsAttackSystem;
import game.systems.physics.PlayerInputSystem;
import game.systems.render.ui.CoordinatesRenderingSystem;
import game.systems.render.world.*;
import game.ui.GUI;
import net.mostlyoriginal.api.network.marshal.common.MarshalStrategy;
import shared.model.lobby.Player;
import shared.network.lobby.player.PlayerLoginRequest;

import static com.artemis.E.E;

public class GameScreen extends ScreenAdapter {

    public static World world;
    public static int player = -1;
    private static GUI gui = new GUI();

    protected FPSLogger logger;
    protected GameState state;
    private static ClientSystem clientSystem;
    private SpriteBatch spriteBatch;

    private static final int FONTS_PRIORITY = WorldConfigurationBuilder.Priority.NORMAL - 1;

    public GameScreen(String host, int port, Player player) {
        this.clientSystem = new ClientSystem(host, port);
        this.spriteBatch = new SpriteBatch();
        this.logger = new FPSLogger();
        clientSystem.start();
        initWorld();
        clientSystem.getKryonetClient().sendToAll(new PlayerLoginRequest(player));
    }

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
                .with(WorldConfigurationBuilder.Priority.NORMAL + 5, new CaveSystem())
                .with(WorldConfigurationBuilder.Priority.NORMAL + 3, new GroundFXsRenderingSystem(spriteBatch))
                .with(WorldConfigurationBuilder.Priority.NORMAL + 3, new ObjectRenderingSystem(spriteBatch))
                .with(WorldConfigurationBuilder.Priority.NORMAL + 3, new ParticleRenderingSystem(spriteBatch))
                .with(WorldConfigurationBuilder.Priority.NORMAL + 2, new CharacterRenderingSystem(spriteBatch))
                .with(WorldConfigurationBuilder.Priority.NORMAL + 1, new FXsRenderingSystem(spriteBatch))
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

    protected void postWorldInit() {
        Entity cameraEntity = world.createEntity();
        E(cameraEntity)
                .aOCamera(true)
                .pos2D();
        world.getSystem(TagManager.class).register("camera", cameraEntity);
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

    protected void drawUI() {
        gui.draw();
    }

    @Override
    public void dispose() {
        gui.dispose();
    }

    public static int getPlayer() {
        return player;
    }

    public static void setPlayer(int player) {
        GameScreen.player = player;
        E entity = E(player);
        entity
                .fXAddParticleEffect(2);
        GUI.getInventory().updateUserInventory();
    }

    public static MarshalStrategy getClient() {
        return clientSystem.getKryonetClient();
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

    public static World getWorld() {
        return world;
    }
}
