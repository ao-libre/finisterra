package game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import game.handlers.AssetHandler;
import game.handlers.StateHandler;
import game.screens.GameScreen;
import game.screens.LoginScreen;
import game.screens.ScreenEnum;
import game.screens.ScreenManager;
import game.systems.network.ClientSystem;
import game.utils.Cursors;
import shared.model.lobby.Player;

/**
 * Represents the game application.
 * Implements {@link ApplicationListener}.
 * <p>
 * This should be the primary instance of the app.
 */
public class AOGame extends Game {

    public static final float GAME_SCREEN_ZOOM = 1f;

    @Override
    public void create() {
        Gdx.app.debug("AOGame", "Creating AOGame...");

        // Load resources & stuff.
        AssetHandler.load();
        if (AssetHandler.getState() == StateHandler.LOADED)
            Gdx.app.debug("AOGame", "Handler loaded!");

        Cursors.setCursor("hand");
        ScreenManager.getInstance().initialize(this);
        toLogin();
    }

    @Override
    public void render() {
        GL20 gl = Gdx.gl;
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1f);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.render();
    }

    public void toGame(String host, int port, Player player) {
        ScreenManager.getInstance().showScreen(ScreenEnum.GAME, host, port, player);
    }

    public void toLogin() {
        ScreenManager.getInstance().showScreen(ScreenEnum.LOGIN);
    }

    public void toLobby(Object... params) {
        ScreenManager.getInstance().showScreen(ScreenEnum.LOBBY, params);
    }

    public void toRoom(Object... params) {
        ScreenManager.getInstance().showScreen(ScreenEnum.ROOM, params);
    }

}
