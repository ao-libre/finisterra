package game.screens;

import com.artemis.SuperMapper;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.managers.TagManager;
import com.artemis.managers.UuidEntityManager;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.FPSLogger;
import com.esotericsoftware.minlog.Log;
import game.AOGame;
import game.ClientConfiguration;
import game.handlers.AOAssetManager;
import game.systems.PlayerSystem;
import game.systems.actions.PlayerActionSystem;
import game.systems.anim.IdleAnimationSystem;
import game.systems.anim.MovementAnimationSystem;
import game.systems.camera.CameraFocusSystem;
import game.systems.camera.CameraMovementSystem;
import game.systems.camera.CameraShakeSystem;
import game.systems.camera.CameraSystem;
import game.systems.input.InputSystem;
import game.systems.map.MapManager;
import game.systems.map.TiledMapSystem;
import game.systems.network.ClientResponseProcessor;
import game.systems.network.ClientSystem;
import game.systems.network.GameNotificationProcessor;
import game.systems.network.TimeSync;
import game.systems.physics.MovementProcessorSystem;
import game.systems.physics.MovementSystem;
import game.systems.physics.PhysicsAttackSystem;
import game.systems.physics.PlayerInputSystem;
import game.systems.render.BatchRenderingSystem;
import game.systems.render.world.*;
import game.systems.resources.*;
import game.systems.screen.ScreenSystem;
import game.systems.sound.SoundSytem;
import game.systems.ui.UserInterfaceSystem;
import game.systems.ui.action_bar.ActionBarSystem;
import game.systems.ui.action_bar.systems.InventorySystem;
import game.systems.ui.action_bar.systems.SpellSystem;
import game.systems.ui.console.ConsoleSystem;
import game.systems.ui.dialog.DialogSystem;
import game.systems.ui.stats.StatsSystem;
import game.systems.ui.user.UserSystem;
import game.systems.world.NetworkedEntitySystem;
import game.systems.world.WorldSystem;
import net.mostlyoriginal.api.system.render.ClearScreenSystem;
import shared.model.map.Tile;

import java.util.concurrent.TimeUnit;

import static com.artemis.WorldConfigurationBuilder.Priority.HIGH;

public class GameScreen extends ScreenAdapter implements WorldScreen {

    private static final int LOGIC = 10;
    private static final int PRE_ENTITY_RENDER_PRIORITY = 6;
    private static final int ENTITY_RENDER_PRIORITY = 5;
    private static final int POST_ENTITY_RENDER_PRIORITY = 4;
    private static final int DECORATION_PRIORITY = 3;
    private static final int UI = 0;

    public static World world;
    private WorldConfigurationBuilder worldConfigBuilder;
    private FPSLogger fpsLogger = new FPSLogger();

    public GameScreen(ClientConfiguration clientConfiguration, AOAssetManager assetManager) {
        long start = System.currentTimeMillis();
        initWorldConfiguration(assetManager, clientConfiguration);
        Log.info("Game screen initialization", "Elapsed time: " + TimeUnit.MILLISECONDS.toSeconds(Math.abs(System.currentTimeMillis() - start)));
    }

    private void initWorldConfiguration(AOAssetManager assetManager, ClientConfiguration clientConfiguration) {
        worldConfigBuilder = new WorldConfigurationBuilder()
                .with(HIGH, new SuperMapper())
                .with(LOGIC,
                        // Player movement
                        new PlayerInputSystem(),
                        new MovementProcessorSystem(),
                        new MovementAnimationSystem(),
                        new IdleAnimationSystem(),
                        new MovementSystem(),
                        new PlayerSystem(),

                        // Camera
                        new CameraSystem(AOGame.GAME_SCREEN_ZOOM),
                        new CameraFocusSystem(),
                        new CameraMovementSystem(),
                        new CameraShakeSystem(),

                        // Logic systems
                        new NetworkedEntitySystem(),
                        new PhysicsAttackSystem(),
                        new SoundSytem(),
                        new TiledMapSystem(),
                        new AnimationsSystem(assetManager),
                        new DescriptorsSystem(assetManager),
                        new MapSystem(),
                        new MusicSystem(),
                        new ObjectSystem(),
                        new ParticlesSystem(),
                        new SoundsSystem(),
                        new SpellsSystem(),
                        new FontsSystem(),
                        new PlayerActionSystem(),
                        new InputSystem(),
                        new ScreenSystem(),
                        new WorldSystem())
                // Rendering
                .with(PRE_ENTITY_RENDER_PRIORITY, new ClearScreenSystem(),
                        new MapGroundRenderingSystem(),
                        new ObjectRenderingSystem(),
                        new TargetRenderingSystem(),
                        new NameRenderingSystem())

                .with(ENTITY_RENDER_PRIORITY, new EffectRenderingSystem(),
                        new CharacterRenderingSystem(),
                        new WorldRenderingSystem())

                .with(POST_ENTITY_RENDER_PRIORITY, new CombatRenderingSystem(),
                        new DialogRenderingSystem(),
                        new MapLastLayerRenderingSystem())

                .with(DECORATION_PRIORITY, new StateRenderingSystem(),
                        new CharacterStatesRenderingSystem(),
                        new BatchRenderingSystem())

                // UI
                .with(UI,
                        new InventorySystem(),
                        new SpellSystem(),
                        new ActionBarSystem(),
                        new ConsoleSystem(),
                        new DialogSystem(),
                        new StatsSystem(),
                        new UserSystem(),
                        new UserInterfaceSystem())

                // Other
                .with(new MapManager(), new TagManager(), new UuidEntityManager(), clientConfiguration);

    }

    public void initWorld(ClientSystem clientSystem) {
        worldConfigBuilder.with(HIGH,
                new ClientResponseProcessor(),
                new GameNotificationProcessor(),
                clientSystem,
                new TimeSync());
        world = new World(worldConfigBuilder.build()); // preload Artemis world
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public void render(float delta) {
        world.setDelta(delta);
        world.process();

        fpsLogger.log();
    }

    @Override
    public void resize(int width, int height) {
        CameraSystem cameraSystem = world.getSystem(CameraSystem.class);
        cameraSystem.camera.viewportWidth = Tile.TILE_PIXEL_WIDTH * 24f;  //We will see width/32f units!
        cameraSystem.camera.viewportHeight = cameraSystem.camera.viewportWidth * height / width;
        cameraSystem.camera.update();

        getWorld().getSystem(UserInterfaceSystem.class).resize(width, height);
        getWorld().getSystem(BatchRenderingSystem.class).resize(width, height);
    }

    @Override
    public void dispose() {
        world.getSystem(ClientSystem.class).stop();
        world.getSystem(UserInterfaceSystem.class).dispose();
    }

}
