package object;

import com.artemis.*;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import entity.character.Character;
import game.screens.CharacterScreen;
import game.screens.GameScreen;
import game.systems.anim.IdleAnimationSystem;
import game.systems.anim.MovementAnimationSystem;
import game.systems.camera.CameraFocusSystem;
import game.systems.camera.CameraMovementSystem;
import game.systems.camera.CameraSystem;
import game.systems.physics.PlayerInputSystem;
import game.systems.render.world.CharacterRenderingSystem;
import object.systems.FaceChangerSystem;

import java.util.Optional;

import static com.artemis.E.E;

public class ObjectCreator extends Game {

    private static final float GAME_SCREEN_ZOOM = 1f;
    private Batch spriteBatch; // This is only used in GameScreen
    private World world;
    private int player;

    @Override
    public void create() {
        Gdx.app.debug("AOGame", "Opening Objects Creator...");
        //AssetHandler.load(assetManager);
        //if (AssetHandler.getState() == StateHandler.LOADED) Gdx.app.debug("AOGame", "Handler loaded!");
        this.spriteBatch = GameScreen.initBatch();
        initWorld();
        postWorldInit();
        addCharacter();
        setScreen(new CharacterScreen(world, player));
    }

    private void initWorld() {
        WorldConfigurationBuilder worldConfigBuilder = new WorldConfigurationBuilder();
        worldConfigBuilder.with(new SuperMapper())
                // Player movement
                .with(new PlayerInputSystem())
                .with(new FaceChangerSystem())
                .with(new MovementAnimationSystem())
                .with(new IdleAnimationSystem())
                // Camera
                .with(new CameraSystem(ObjectCreator.GAME_SCREEN_ZOOM))
                .with(new CameraFocusSystem())
                .with(new CameraMovementSystem())
                // Logic systems
                .with(new TagManager())
                // Rendering
//                .with(WorldConfigurationBuilder.Priority.NORMAL + 2, new AnimationRenderingSystem(spriteBatch));
                .with(WorldConfigurationBuilder.Priority.NORMAL + 2, new CharacterRenderingSystem(spriteBatch));

        world = new World(worldConfigBuilder.build()); // preload Artemis world
    }

    private void postWorldInit() {
        Entity cameraEntity = world.createEntity();
        E(cameraEntity)
                .aOCamera(true)
                .pos2D();
        world.getSystem(TagManager.class).register("camera", cameraEntity);
    }

    private void addCharacter() {
        player = world.create();
        E(player)
                .headIndex(4)
                .bodyIndex(101)
                //.shieldIndex(1)
                //.weaponIndex(3)
                .heading()
                .moving(true)
                .aOPhysics()
                .focused()
                .worldPos()
                .character();
    }

    public int getPlayer() {
        return player;
    }

    @Override
    public void render() {
        GL20 gl = Gdx.gl;
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        EBag es = E.withComponent(Character.class);
        CharacterRenderingSystem system = world.getSystem(CharacterRenderingSystem.class);
        system.getBatch().begin();
        for (E e : es) {
            system.drawPlayer(e, Optional.empty());
        }
        system.getBatch().end();
        super.render();
    }

}
