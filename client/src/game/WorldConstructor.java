package game;

import com.artemis.*;
import com.artemis.managers.TagManager;
import com.artemis.managers.UuidEntityManager;
import game.handlers.DefaultAOAssetManager;
import game.screens.ScreenEnum;
import game.screens.ScreenManager;
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

import java.util.Arrays;

import static com.artemis.WorldConfigurationBuilder.Priority.HIGH;

public class WorldConstructor {

    private static final int LOGIC = 10;
    private static final int PRE_ENTITY_RENDER_PRIORITY = 6;
    private static final int ENTITY_RENDER_PRIORITY = 5;
    private static final int POST_ENTITY_RENDER_PRIORITY = 4;
    private static final int DECORATION_PRIORITY = 3;
    private static final int UI = 0;

    private static WorldConfiguration getWorldConfiguration(ClientConfiguration clientConfiguration, ScreenManager screenManager, DefaultAOAssetManager assetManager) {
        return new WorldConfigurationBuilder()
                // Sistemas de uso global (no necesitan prioridad porque son pasivos)
                .with(clientConfiguration,
                        screenManager)

                // register all screens
                .with(Arrays.stream(ScreenEnum.values())
                        .map(ScreenEnum::get)
                        .map(BaseSystem.class::cast)
                        .toArray(BaseSystem[]::new))

                // Network system (no necesita prioridad porque es asincrónico, funciona por callbacks)
                .with(new ClientSystem(),
                        new ClientResponseProcessor(),
                        new GameNotificationProcessor())

                // Sistemas de alta prioridad
                .with(HIGH,
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
                        new AnimationsSystem(),
                        new DescriptorsSystem(),
                        new MessageSystem(),
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

                // Otros sistemas
                .with(new MapManager(),
                        new TagManager(),
                        new UuidEntityManager())

                .build()
                .register(assetManager);
    }

    /**
     * Construye el Artemis World, inicializa e inyecta sistemas.
     * Este método es bloqueante.
     */
    public static void create(ClientConfiguration clientConfiguration,
                              ScreenManager screenManager, DefaultAOAssetManager assetManager) {
        new World(getWorldConfiguration(clientConfiguration, screenManager, assetManager));
    }
}
