package game.screens;

import com.artemis.SuperMapper;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.managers.TagManager;
import com.artemis.managers.UuidEntityManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.FPSLogger;
import com.esotericsoftware.minlog.Log;
import game.AOGame;
import game.ClientConfiguration;
import game.handlers.AOAssetManager;
import game.systems.PlayerSystem;
import game.systems.network.*;
import shared.systems.IntervalSystem;
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
import game.systems.physics.MovementProcessorSystem;
import game.systems.physics.MovementSystem;
import game.systems.physics.AttackAnimationSystem;
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
import shared.model.map.Tile;

import java.util.concurrent.TimeUnit;

import static com.artemis.WorldConfigurationBuilder.Priority.HIGH;

public class GameScreen extends ScreenAdapter implements WorldScreen {

    private World world;
    private FPSLogger fpsLogger = new FPSLogger();

    public GameScreen() {
        // @todo refactorizar este acceso estático
        AOGame game = (AOGame) Gdx.app.getApplicationListener();
        this.world = game.getWorld();
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public void render(float delta) {
        world.setDelta(delta);
        world.process();

        fpsLogger.log();
    }

    @Override
    public void resize(int width, int height) {
        CameraSystem cameraSystem = world.getSystem(CameraSystem.class);
        cameraSystem.camera.viewportWidth = Tile.TILE_PIXEL_WIDTH * 24f;  //We will see width/32f units!
        cameraSystem.camera.viewportHeight = cameraSystem.camera.viewportWidth * height / width;
        cameraSystem.camera.update();

        getWorld().getSystem(UserInterfaceSystem.class).resize(width, height);
        getWorld().getSystem(BatchRenderingSystem.class).resize(width, height);
    }

    @Override
    public void dispose() {
        // TODO CHECK
        world.getSystem(ClientSystem.class).stop();
        world.getSystem(UserInterfaceSystem.class).dispose();
    }

}
