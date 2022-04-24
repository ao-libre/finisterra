package game.systems.input;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import game.screens.ScreenManager;
import game.systems.actions.PlayerActionSystem;
import game.systems.camera.CameraSystem;
import game.systems.map.MapManager;
import game.systems.network.ClientSystem;
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

    @Wire private MusicSystem musicSystem;

    private PlayerActionSystem playerActionSystem;
    private CameraSystem cameraSystem;
    private ScreenSystem screenSystem;
    private ScreenManager screenManager;
    private ClientSystem clientSystem;

    private InventorySystem inventorySystem;
    private SpellSystem spellSystem;
    private ActionBarSystem actionBarSystem;
    private DialogSystem dialogSystem;
    private MouseSystem mouseSystem;

    private boolean shiftLeftPressed = false;


    @Override
    public boolean keyDown(int keycode) {
        doActionsOnKeyDown(keycode);
        return false;
    }

    private void doActionsOnKeyDown(int keycode) {
        switch(keycode) {
            case Input.Keys.SHIFT_LEFT:
                shiftLeftPressed = true;
                break;
        }
    }

    @Override
    public boolean keyUp(int keycode) {
        if (alternativeKeys) {
            doAlternativeActions(keycode);
        } else {
            doActionsOnKeyUp(keycode);
        }
        switch (keycode) {
            case AlternativeKeys.TALK:
                dialogSystem.talk();
                break;
            case Input.Keys.F1:
                alternativeKeys = !alternativeKeys;
                break;
            case Input.Keys.ESCAPE:
                clientSystem.logout();
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
        doActionOnTouchDown(screenX, screenY, pointer, button);
        return false;
    }

    private void doActionOnTouchDown(int screenX, int screenY, int pointer, int button) {
        if(shiftLeftPressed){
            playerActionSystem.teleport();
        }
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            mouseSystem.onClick();
            return true;
        }
        return false;
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
    public boolean scrolled(float amountX, float amountY) {
        cameraSystem.zoom(amountY, CameraSystem.ZOOM_TIME);
        return true;
    }

    private void doActionsOnKeyUp(int keycode) {
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
            case Input.Keys.F2:
                screenSystem.takeScreenshot();
                break;
            case Input.Keys.F4:
                MapManager.layer4Disable = !MapManager.layer4Disable;
                break;
            case Input.Keys.F11:
                screenSystem.toggleFullscreen();
                break;
            case Input.Keys.NUM_7:
                musicSystem.toggle();
                break;
            case Input.Keys.NUM_8:
                musicSystem.volumeDown();
                break;
            case Input.Keys.NUM_9:
                musicSystem.volumeUp();
                break;
            case Input.Keys.SHIFT_LEFT:
                shiftLeftPressed = false;
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
            case Input.Keys.F2:
                screenSystem.takeScreenshot();
                break;
            case Input.Keys.F4:
                MapManager.layer4Disable = !MapManager.layer4Disable;
                break;
            case Input.Keys.F11:
                screenSystem.toggleFullscreen();
                break;

            case Input.Keys.NUM_7:
                musicSystem.toggle();
                break;
            case Input.Keys.NUM_8:
                musicSystem.volumeDown();
                break;
            case Input.Keys.NUM_9:
                musicSystem.volumeUp();
                break;
        }
    }
}
