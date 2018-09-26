package ar.com.tamborindeguy.client.game;

import ar.com.tamborindeguy.client.screens.GameScreen;
import ar.com.tamborindeguy.client.screens.LoginScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class AO extends Game {

    public static final String GAME_GRAPHICS_PATH = "data/graficos/";
    public static final String GAME_FXS_PATH = "data/fxs/";
    public static final String GAME_FONTS_PATH = "data/fonts/";
    public static final String GAME_MAPS_PATH = "data/mapas/";
    public static final String GAME_INIT_PATH = "data/init/";
    public static final String GAME_SHADERS_PATH = "data/shaders/";
    public static final String GAME_GRAPHICS_EXTENSION = ".png";
    public static final String GAME_SHADERS_LIGHT = "light.png";

    public static final int GAME_SCREEN_WIDTH = 1280;
    public static final int GAME_SCREEN_HEIGHT = 720;
    public static final float GAME_SCREEN_ZOOM = 1.8f;
    public static final boolean GAME_FULL_SCREEN = false;
    public static final boolean GAME_VSYNC_ENABLED = true;

    protected SpriteBatch spriteBatch;
    private GameScreen gameScreen;

    @Override
    public void create() {
        this.spriteBatch = new SpriteBatch();
        setScreen(new LoginScreen(this));
    }

    @Override
    public void render() {
        GL20 gl = Gdx.gl;
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.render();
    }

    public void showGameScreen() {
        setScreen(gameScreen);
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public void setGameScreen(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }
}