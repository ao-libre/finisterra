package ar.com.tamborindeguy.client.screens;

import ar.com.tamborindeguy.client.game.AO;
import ar.com.tamborindeguy.client.systems.anim.MovementAnimationSystem;
import ar.com.tamborindeguy.client.systems.camera.CameraFocusSystem;
import ar.com.tamborindeguy.client.systems.camera.CameraMovementSystem;
import ar.com.tamborindeguy.client.systems.camera.CameraSystem;
import ar.com.tamborindeguy.client.systems.interactions.DialogSystem;
import ar.com.tamborindeguy.client.systems.interactions.MeditateSystem;
import ar.com.tamborindeguy.client.systems.map.TiledMapSystem;
import ar.com.tamborindeguy.client.systems.network.ClientSystem;
import ar.com.tamborindeguy.client.systems.physics.MovementProcessorSystem;
import ar.com.tamborindeguy.client.systems.physics.MovementSystem;
import ar.com.tamborindeguy.client.systems.physics.PhysicsAttackSystem;
import ar.com.tamborindeguy.client.systems.physics.PlayerInputSystem;
import ar.com.tamborindeguy.client.systems.render.ui.CoordinatesRenderingSystem;
import ar.com.tamborindeguy.client.systems.render.world.*;
import com.artemis.Entity;
import com.artemis.SuperMapper;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.managers.TagManager;
import com.artemis.managers.UuidEntityManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import net.mostlyoriginal.api.network.marshal.common.MarshalStrategy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.artemis.E.E;

public class GameScreen extends WorldScreen {

    public static final int FONTS_PRIORITY = WorldConfigurationBuilder.Priority.NORMAL + 3;

    private Stage stage;
    private Table dialog;
    private Table inventory;

    public static ClientSystem client;
    public static int player;

    public static Map<Integer, Integer> networkedEntities = new HashMap<>();

    public GameScreen(AO game, ClientSystem client) {
        super(game);
        this.client = client;
        init();
    }

    public static int getPlayer() {
        return player;
    }

    public static void setPlayer(int player) {
        GameScreen.player = player;
        E(player).character();
    }

    @Override
    protected void postWorldInit() {
        Gdx.input.setInputProcessor(stage);
        Entity cameraEntity = world.createEntity();
        E(cameraEntity)
                .aOCamera(true)
                .pos2D();
        world.getSystem(TagManager.class).register("camera", cameraEntity);
    }

    @Override
    protected void initSystems(WorldConfigurationBuilder builder) {
        builder
                .with(new SuperMapper())
                .with(client)
                // Player movement
                .with(new PlayerInputSystem())
                .with(new MovementProcessorSystem())
                .with(new MovementAnimationSystem())
                .with(new MovementSystem())
                // Camera
                .with(new CameraSystem(AO.GAME_SCREEN_ZOOM))
                .with(new CameraFocusSystem())
                .with(new CameraMovementSystem())
                // Logic systems
                .with(new PhysicsAttackSystem())
                .with(new MeditateSystem())
                .with(new DialogSystem(dialog))
                // Rendering
                .with(WorldConfigurationBuilder.Priority.NORMAL + 5, new TiledMapSystem())
                .with(WorldConfigurationBuilder.Priority.NORMAL + 4, new MapLowerLayerRenderingSystem(this.game.getSpriteBatch()))
                .with(WorldConfigurationBuilder.Priority.NORMAL + 3, new ObjectRenderingSystem(this.game.getSpriteBatch()))
                .with(WorldConfigurationBuilder.Priority.NORMAL + 2, new CharacterRenderingSystem(this.game.getSpriteBatch()))
                .with(WorldConfigurationBuilder.Priority.NORMAL + 1, new FXsRenderingSystem(this.game.getSpriteBatch()))
                .with(WorldConfigurationBuilder.Priority.NORMAL + 1, new MapUpperLayerRenderingSystem(this.game.getSpriteBatch()))
                .with(new StateRenderingSystem(game.getSpriteBatch()))
                .with(new CharacterStatusRenderingSystem(game.getSpriteBatch()))
                .with(FONTS_PRIORITY, new NameRenderingSystem(game.getSpriteBatch()))
                .with(FONTS_PRIORITY, new DialogRenderingSystem(game.getSpriteBatch()))
                .with(FONTS_PRIORITY, new CoordinatesRenderingSystem(game.getSpriteBatch()))
                .with(FONTS_PRIORITY, new CharacterStatesRenderingSystem(game.getSpriteBatch()))
                .with(FONTS_PRIORITY, new CombatRenderingSystem(game.getSpriteBatch()))
                // Other
                .with(new TagManager())
                .with(new UuidEntityManager());
        // WORLD SYSTEMS
    }

    @Override
    protected void initScene() {
        stage = new Stage();
        Container<Table> dialogContainer = createDialogContainer();
        stage.addActor(dialogContainer);
        Container<Table> inventory = createInventory();
    }

    private Container<Table> createInventory() {
        Container<Table> dialogContainer = new Container<Table>();

        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float containerW = screenW * 0.2f;
        dialogContainer.setWidth(containerW);
        // square for now
        dialogContainer.setHeight(containerW);
        dialogContainer.setPosition((screenW - containerW), screenH * 0.5f - (containerW / 2));
        inventory = new Table();
        dialogContainer.setActor(inventory);
        return dialogContainer;
    }

    private Container<Table> createDialogContainer() {
        Container<Table> dialogContainer = new Container<Table>();

        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float containerW = screenW * 0.8f;
        dialogContainer.setWidth(containerW);
        dialogContainer.setPosition((screenW - containerW) / 2.0f, screenH * 0.25f);
        dialogContainer.fillX();
        dialog = new Table();
        dialogContainer.setActor(dialog);
        return dialogContainer;
    }

    public static World getWorld() {
        return world;
    }

    public static MarshalStrategy getClient() {
        return client.getMarshal();
    }

    @Override
    protected void update(float deltaTime) {
        this.logger.log();

        world.setDelta(MathUtils.clamp(deltaTime, 0, 1 / 16f));
        this.world.process();

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
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public static boolean entityExsists(int networkId) {
        return networkedEntities.containsKey(networkId);
    }

    public static int getNetworkedEntity(int networkId) {
        return networkedEntities.get(networkId);
    }

    public static Set<Integer> getEntities() {
        return new HashSet<>(networkedEntities.values());
    }

    public static void registerEntity(int networkId, int entityId) {
        networkedEntities.put(networkId, entityId);
    }

    public static void unregisterEntity(int networkId) {
        int entityId = networkedEntities.get(networkId);
        world.delete(entityId);
        networkedEntities.remove(networkId);
    }

    @Override
    public void dispose() {
        stage.dispose();
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
