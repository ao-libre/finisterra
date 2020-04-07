package game;

import com.artemis.SuperMapper;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.managers.TagManager;
import com.artemis.managers.UuidEntityManager;
import game.handlers.AOAssetManager;
import game.screens.ScreenManager;
import game.screens.WorldScreen;
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
import game.systems.physics.AttackAnimationSystem;
import game.systems.physics.MovementProcessorSystem;
import game.systems.physics.MovementSystem;
import game.systems.physics.PlayerInputSystem;
import game.systems.render.BatchRenderingSystem;
import game.systems.render.world.*;
import game.systems.resources.*;
import game.systems.screen.MouseSystem;
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
import game.systems.world.ClearSystem;
import game.systems.world.NetworkedEntitySystem;
import game.systems.world.WorldSystem;
import game.utils.CursorSystem;
import net.mostlyoriginal.api.system.render.ClearScreenSystem;
import shared.systems.IntervalSystem;

import static com.artemis.WorldConfigurationBuilder.Priority.HIGH;

public class WorldConstructor implements WorldScreen {

    private World world;

    private static final int LOGIC = 10;
    private static final int PRE_ENTITY_RENDER_PRIORITY = 6;
    private static final int ENTITY_RENDER_PRIORITY = 5;
    private static final int POST_ENTITY_RENDER_PRIORITY = 4;
    private static final int DECORATION_PRIORITY = 3;
    private static final int UI = 0;

    private WorldConfigurationBuilder initWorldConfiguration(AOGame game,
                                                             AOAssetManager assetManager,
                                                             ClientSystem clientSystem,
                                                             ClientConfiguration clientConfiguration) {
        return new WorldConfigurationBuilder()
                .with(HIGH,
                        new ClientResponseProcessor(),
                        new GameNotificationProcessor(),
                        clientSystem,
                        new TimeSync(),
                        new SuperMapper(),
                        new ClearSystem())
                .with(LOGIC,
                        new IntervalSystem(),
                        // Player component.movement
                        new PlayerInputSystem(),
                        new MovementProcessorSystem(),
                        new MovementAnimationSystem(),
                        new IdleAnimationSystem(),
                        new MovementSystem(),
                        new PlayerSystem(),

                        // Camera
                        new CameraSystem(),
                        new CameraFocusSystem(),
                        new CameraMovementSystem(),
                        new CameraShakeSystem(),

                        // Logic systems
                        new NetworkedEntitySystem(),
                        new AttackAnimationSystem(),
                        new SoundSytem(),
                        new TiledMapSystem(),
                        new AnimationsSystem(assetManager),
                        new DescriptorsSystem(assetManager),
                        new MessageSystem(assetManager),
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
                .with(PRE_ENTITY_RENDER_PRIORITY,
                        new ClearScreenSystem(),
                        new MapGroundRenderingSystem(),
                        new ObjectRenderingSystem(),
                        new TargetRenderingSystem(),
                        new NameRenderingSystem())

                .with(ENTITY_RENDER_PRIORITY,
                        new EffectRenderingSystem(),
                        new CharacterRenderingSystem(),
                        new WorldRenderingSystem())

                .with(POST_ENTITY_RENDER_PRIORITY,
                        new CombatRenderingSystem(),
                        new DialogRenderingSystem(),
                        new MapLastLayerRenderingSystem())

                .with(DECORATION_PRIORITY,
                        new StateRenderingSystem(),
                        new CharacterStatesRenderingSystem(),
                        new BatchRenderingSystem())
                // UI
                .with(UI,
                        new MouseSystem(),
                        new CursorSystem(),
                        new InventorySystem(),
                        new SpellSystem(),
                        new ActionBarSystem(),
                        new ConsoleSystem(),
                        new DialogSystem(),
                        new StatsSystem(),
                        new UserSystem(),
                        new UserInterfaceSystem())

                // Other
                .with(new MapManager(),
                        new TagManager(),
                        new UuidEntityManager(),
                        clientConfiguration,
                        new ScreenManager(game));

    }

    public WorldConstructor(AOGame game,
                            AOAssetManager assetManager,
                            ClientSystem clientSystem,
                            ClientConfiguration clientConfiguration) {

        WorldConfiguration builtWorld = initWorldConfiguration(game, assetManager, clientSystem, clientConfiguration).build();
        this.world = new World(builtWorld);
    }

    @Override
    public World getWorld() {
        return world;
    }
}