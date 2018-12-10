package ar.com.tamborindeguy.client.screens;

import ar.com.tamborindeguy.client.game.AOGame;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.FPSLogger;

public abstract class WorldScreen extends ScreenAdapter {

    public static final int GAME_RUNNING = 0;
    public static final int GAME_PAUSED = 1;

    public static AOGame game;
    public static World world;
    protected final WorldConfigurationBuilder builder = new WorldConfigurationBuilder();
    protected FPSLogger logger;
    protected int state;

    public WorldScreen(AOGame game) {
        WorldScreen.game = game;
        this.logger = new FPSLogger();
    }

    public void init() {
        this.initSystems(builder);
        this.initScene();
        world = new World(builder.build());
    }

    @Override
    public void show() {

        this.postWorldInit();
        this.state = GAME_RUNNING;
    }

    protected abstract void postWorldInit();

    @Override
    public void render(float delta) {
        this.update(delta);
        this.drawUI();
    }

    @Override
    public void pause() {
        if (this.state == GAME_RUNNING) {
            this.state = GAME_PAUSED;
            this.pauseSystems();
        }
    }

    @Override
    public void resume() {
        if (this.state == GAME_PAUSED) {
            this.state = GAME_RUNNING;
            this.resumeSystems();
        }
    }

    @Override
    public void dispose() {
        world.dispose();
        super.dispose();
    }

    abstract protected void initSystems(WorldConfigurationBuilder builder);

    abstract protected void initScene();

    abstract protected void resumeSystems();

    abstract protected void pauseSystems();

    abstract protected void updatePaused();

    abstract protected void updateRunning(float deltaTime);

    abstract protected void update(float deltaTime);

    abstract protected void drawUI();

    public AOGame getGame() {
        return game;
    }

    public void setGame(AOGame game) {
        WorldScreen.game = game;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
