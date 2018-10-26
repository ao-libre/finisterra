package ar.com.tamborindeguy.client.screens;

import ar.com.tamborindeguy.client.game.AO;
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
import ar.com.tamborindeguy.client.ui.GUI;
import com.artemis.Entity;
import com.artemis.SuperMapper;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.managers.TagManager;
import com.artemis.managers.UuidEntityManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.network.marshal.common.MarshalStrategy;

import static com.artemis.E.E;

public class GameScreen extends WorldScreen {

    private static final int FONTS_PRIORITY = WorldConfigurationBuilder.Priority.NORMAL - 1;
    public static ClientSystem client;
    public static int player;
    private static GUI gui;

    public GameScreen(AO game, ClientSystem client) {
        super(game);
        GameScreen.client = client;
        init();
    }

    public static int getPlayer() {
        return player;
    }

    public static void setPlayer(int player) {
        GameScreen.player = player;
        GUI.getInventory().updateUserInventory();
    }

    public static World getWorld() {
        return world;
    }

    public static MarshalStrategy getClient() {
        return client.getMarshal();
    }

    @Override
    protected void initSystems(WorldConfigurationBuilder builder) {
        builder.with(new SuperMapper())
                .with(client)
                // Player movement
                .with(new PlayerInputSystem())
                .with(new MovementProcessorSystem())
                .with(new MovementAnimationSystem())
                .with(new IdleAnimationSystem())
                .with(new MovementSystem())
                // Camera
                .with(new CameraSystem(AO.GAME_SCREEN_ZOOM))
                .with(new CameraFocusSystem())
                .with(new CameraMovementSystem())
                // Logic systems
                .with(new PhysicsAttackSystem())
                // Rendering
                .with(WorldConfigurationBuilder.Priority.NORMAL + 5, new TiledMapSystem())
                .with(WorldConfigurationBuilder.Priority.NORMAL + 4, new MapLowerLayerRenderingSystem(game.getSpriteBatch()))
                .with(WorldConfigurationBuilder.Priority.NORMAL + 3, new ObjectRenderingSystem(game.getSpriteBatch()))
                .with(WorldConfigurationBuilder.Priority.NORMAL + 2, new CharacterRenderingSystem(game.getSpriteBatch()))
                .with(WorldConfigurationBuilder.Priority.NORMAL + 1, new FXsRenderingSystem(game.getSpriteBatch()))
                .with(WorldConfigurationBuilder.Priority.NORMAL + 1, new MapUpperLayerRenderingSystem(game.getSpriteBatch()))
                .with(WorldConfigurationBuilder.Priority.NORMAL, new CoordinatesRenderingSystem(game.getSpriteBatch()))
                .with(FONTS_PRIORITY, new StateRenderingSystem(game.getSpriteBatch()))
                .with(FONTS_PRIORITY, new CharacterStatusRenderingSystem(game.getSpriteBatch()))
                .with(FONTS_PRIORITY, new CombatRenderingSystem(game.getSpriteBatch()))
                .with(FONTS_PRIORITY, new NameRenderingSystem(game.getSpriteBatch()))
                .with(FONTS_PRIORITY, new DialogRenderingSystem(game.getSpriteBatch()))
                .with(FONTS_PRIORITY, new CharacterStatesRenderingSystem(game.getSpriteBatch()))
                // Other
                .with(new TagManager())
                .with(new UuidEntityManager()); // why?
    }

    @Override
    protected void initScene() {
        gui = new GUI();
        gui.initialize();
    }

    @Override
    protected void postWorldInit() {

        Entity cameraEntity = world.createEntity();
        E(cameraEntity)
                .aOCamera(true)
                .pos2D();
        world.getSystem(TagManager.class).register("camera", cameraEntity);
    }

    @Override
    protected void update(float deltaTime) {
        this.logger.log();

        world.setDelta(MathUtils.clamp(deltaTime, 0, 1 / 16f));
        world.process();

        switch (this.state) {
            case GAME_RUNNING: {
                updateRunning(deltaTime);
                break;
            }
            case GAME_PAUSED: {
                updatePaused();
                break;
            }
        }
    }

    @Override
    protected void drawUI() {
        gui.draw();
    }

    @Override
    public void dispose() {
        gui.dispose();
    }

    @Override
    protected void updateRunning(float deltaTime) {
        //
    }

    @Override
    protected void updatePaused() {
        //
    }

    @Override
    protected void pauseSystems() {
        //
    }

    @Override
    protected void resumeSystems() {
        //
    }

}
