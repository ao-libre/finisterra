package game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.esotericsoftware.minlog.Log;
import game.handlers.AssetHandler;
import game.handlers.StateHandler;
import game.screens.ScreenEnum;
import game.screens.ScreenManager;
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
    public static final float GAME_SCREEN_MAX_ZOOM = 1.3f;

    @Override
    public void create() {
        Gdx.app.debug("AOGame", "Creating AOGame...");

        // @todo load platform-independent configuration (network, etc.)

        // Load resources & stuff.
        long start = System.currentTimeMillis();
        AssetHandler.load();
        if (AssetHandler.getState() == StateHandler.LOADED)
            Gdx.app.debug("AOGame", "Handler loaded!");
        Gdx.app.log("Client initialization", "Elapsed time: " + (System.currentTimeMillis() - start));
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

    public void dispose() {
        Log.info("Closing client...");
        AssetHandler.unload();
        Gdx.app.exit();
        Log.info("Thank you for playing! See you soon...");
        System.exit(0);
    }
}
