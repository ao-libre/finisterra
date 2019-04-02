package game;

import com.artemis.SuperMapper;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.managers.TagManager;
import com.artemis.managers.UuidEntityManager;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import game.handlers.AssetHandler;
import game.handlers.StateHandler;
import game.screens.GameScreen;
import game.screens.LoginScreen;
import game.screens.ScreenEnum;
import game.screens.ScreenManager;
import game.systems.anim.IdleAnimationSystem;
import game.systems.anim.MovementAnimationSystem;
import game.systems.camera.CameraFocusSystem;
import game.systems.camera.CameraMovementSystem;
import game.systems.camera.CameraSystem;
import game.systems.map.MapSystem;
import game.systems.network.ClientSystem;
import game.systems.physics.MovementProcessorSystem;
import game.systems.physics.MovementSystem;
import game.systems.physics.PhysicsAttackSystem;
import game.systems.physics.PlayerInputSystem;
import game.systems.render.ui.CoordinatesRenderingSystem;
import game.systems.render.world.*;
import shared.model.lobby.Player;

/**
 * Represents the game application.
 * Implements {@link ApplicationListener}.
 * <p>
 * This should be the primary instance of the app.
 */
public class AOGame extends Game {

    public static final float GAME_SCREEN_ZOOM = 1.8f;
    private ClientSystem clientSystem; // Marshal client system (Kryonet)

    // Screens
    private GameScreen gameScreen; // Game main screen
    private LoginScreen loginScreen;

    @Override
    public void create() {
        Gdx.app.debug("AOGame", "Creating AOGame...");

        // Load resources & stuff.
        AssetHandler.load();
        if (AssetHandler.getState() == StateHandler.LOADED)
            Gdx.app.debug("AOGame", "Handler loaded!");

        ScreenManager.getInstance().initialize(this);
        toLogin();
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

    public ClientSystem getClientSystem() {
        return clientSystem;
    }
}
