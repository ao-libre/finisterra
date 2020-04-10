package game.systems.input;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import game.AOGame;
import game.screens.ScreenEnum;
import game.screens.ScreenManager;
import game.systems.actions.PlayerActionSystem;
import game.systems.camera.CameraSystem;
import game.systems.resources.MusicSystem;
import game.systems.screen.MouseSystem;
import game.systems.screen.ScreenSystem;
import game.systems.ui.action_bar.ActionBarSystem;
import game.systems.ui.action_bar.systems.InventorySystem;
import game.systems.ui.action_bar.systems.SpellSystem;
import game.systems.ui.dialog.DialogSystem;
import game.utils.AOKeys;
import game.utils.AlternativeKeys;
import net.mostlyoriginal.api.system.core.PassiveSystem;

@Wire
public class InputSystem extends PassiveSystem implements InputProcessor {

    public static boolean alternativeKeys = false;

    private PlayerActionSystem playerActionSystem;
    private CameraSystem cameraSystem;
    private ScreenSystem screenSystem;
    private ScreenManager screenManager;

    private InventorySystem inventorySystem;
    private SpellSystem spellSystem;
    private ActionBarSystem actionBarSystem;
    private MusicSystem musicSystem;
    private DialogSystem dialogSystem;
    private MouseSystem mouseSystem;


    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (alternativeKeys) {
            doAlternativeActions(keycode);
        } else {
            doActions(keycode);
        }
        switch (keycode) {
            case AlternativeKeys.TALK:
                dialogSystem.talk();
                break;
            case Input.Keys.F1:
                alternativeKeys = !alternativeKeys;
                break;
        }

        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        mouseSystem.onClick();
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        cameraSystem.zoom(amount, CameraSystem.ZOOM_TIME);
        return true;
    }


    private void doActions(int keycode) {
        switch (keycode) {
            case AOKeys.INVENTORY:
                actionBarSystem.showInventory();
                break;
            case AOKeys.SPELLS:
                actionBarSystem.showSpells();
                break;
            case AOKeys.MEDITATE:
                playerActionSystem.meditate();
                break;
            case AOKeys.DROP:
                inventorySystem.dropItem();
                break;
            case AOKeys.TAKE:
                inventorySystem.takeItem();
                break;
            case AOKeys.EQUIP:
                inventorySystem.equip();
                break;
            case AOKeys.USE:
                inventorySystem.use();
                break;
            case AOKeys.ATTACK_1:
            case AOKeys.ATTACK_2:
                playerActionSystem.attack();
                break;
            case Input.Keys.L:
                actionBarSystem.toggle();
                break;
            case Input.Keys.ESCAPE:
                // Disconnect & go back to LoginScreen
                screenManager.to(ScreenEnum.LOGIN);
                break;
            case Input.Keys.F2:
                screenSystem.takeScreenshot();
                break;
            case Input.Keys.F11:
                screenSystem.toggleFullscreen();
                break;
            case Input.Keys.NUM_7:
                musicSystem.toggle();
                break;
            case Input.Keys.NUM_8:
                musicSystem.volumeDown(-0.1f);
                break;
            case Input.Keys.NUM_9:
                musicSystem.volumeUp(0.1f);
                break;

        }
    }

    private void doAlternativeActions(int keycode) {
        switch (keycode) {
            case AlternativeKeys.INVENTORY:
                actionBarSystem.showInventory();
                break;
            case AlternativeKeys.SPELLS:
                actionBarSystem.showSpells();
                break;

            case AlternativeKeys.MEDITATE:
                playerActionSystem.meditate();
                break;
            case AlternativeKeys.DROP:
                inventorySystem.dropItem();
                break;
            case AlternativeKeys.TAKE:
                inventorySystem.takeItem();
                break;
            case AlternativeKeys.EQUIP:
                inventorySystem.equip();
                break;
            case AlternativeKeys.USE:
                inventorySystem.use();
                break;
            case AlternativeKeys.ATTACK_1:
            case AlternativeKeys.ATTACK_2:
                playerActionSystem.attack();
                break;
            case Input.Keys.ESCAPE:
                AOGame game = (AOGame) Gdx.app.getApplicationListener();

                break;
            case Input.Keys.F2:
                screenSystem.takeScreenshot();
                break;
            case Input.Keys.F11:
                screenSystem.toggleFullscreen();
                break;

            case Input.Keys.NUM_7:
                musicSystem.toggle();
                break;
            case Input.Keys.NUM_8:
                musicSystem.volumeDown(-0.1f);
                break;
            case Input.Keys.NUM_9:
                musicSystem.volumeUp(0.1f);
                break;

        }
    }

}
