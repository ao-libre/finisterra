package game.screens;

import game.AOGame;
import game.ui.GUI;
import com.artemis.*;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.network.marshal.common.MarshalStrategy;

import static com.artemis.E.E;

public class GameScreen extends ScreenAdapter {

    public static World world;
    protected FPSLogger logger;
    protected GameState state;
    //
    public static int player = -1;
    private static GUI gui;

    public GameScreen(World world) {
        this.world = world;
        this.logger = new FPSLogger();
        gui = new GUI();
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
        this.drawUI();
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
                .aOPhysics() //
                .focused() //
                .canWrite() //
                .fXAddParticleEffect(2);
        GUI.getInventory().updateUserInventory();
    }

    public static MarshalStrategy getClient() {
        AOGame game = (AOGame) Gdx.app.getApplicationListener();
        return game.getClientSystem().getMarshal();
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

    public void setGame(AOGame game) {
        WorldScreen.game = game;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public static World getWorld() {
        return world;
    }
}
