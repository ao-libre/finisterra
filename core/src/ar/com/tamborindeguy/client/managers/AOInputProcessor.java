package ar.com.tamborindeguy.client.managers;

import ar.com.tamborindeguy.client.screens.GameScreen;
import ar.com.tamborindeguy.client.ui.GUI;
import ar.com.tamborindeguy.client.utils.Keys;
import ar.com.tamborindeguy.model.AttackType;
import ar.com.tamborindeguy.network.combat.AttackRequest;
import ar.com.tamborindeguy.network.interaction.DropItem;
import ar.com.tamborindeguy.network.interaction.MeditateRequest;
import ar.com.tamborindeguy.network.interaction.TakeItemRequest;
import ar.com.tamborindeguy.network.interaction.TalkRequest;
import ar.com.tamborindeguy.network.inventory.ItemActionRequest;
import com.artemis.E;
import com.badlogic.gdx.scenes.scene2d.Stage;

import static com.artemis.E.E;

public class AOInputProcessor extends Stage {

    @Override
    public boolean keyUp(int keycode) {
        E player = E(GameScreen.getPlayer());
        if (!(player.isWriting())) {
            switch (keycode) {
                case Keys.INVENTORY:
                    toggleInventory();
                    break;
                case Keys.MEDITATE:
                    toggleMeditate();
                    break;
                case Keys.DROP:
                    dropItem();
                    break;
                case Keys.TAKE:
                    takeItem();
                    break;
                case Keys.EQUIP:
                    equip();
                    break;
                case Keys.ATTACK_1:
                    attack();
                    break;
                case Keys.ATTACK_2:
                    attack();
                    break;

            }
        }
        switch (keycode) {
            case Keys.TALK:
                toggleDialogText();
                break;
        }

        return super.keyUp(keycode);
    }

    private void attack() {
        E player = E(GameScreen.getPlayer());
        if (!player.hasAttack() || player.getAttack().interval - GameScreen.getWorld().getDelta() <= 0) {
            GameScreen.getClient().sendToAll(new AttackRequest(AttackType.PHYSICAL));
            player.attackInterval();
        }
    }

    private void equip() {
        GUI.getInventory().getSelected().ifPresent(slot -> {
            GameScreen.getClient().sendToAll(new ItemActionRequest(GUI.getInventory().selectedIndex()));
        });
    }

    private void takeItem() {
        GameScreen.getClient().sendToAll(new TakeItemRequest());
    }

    // drop selected item (count 1 for the time being)
    private void dropItem() {
        GUI.getInventory().getSelected().ifPresent(selected -> {
            int player = GameScreen.getPlayer();
            GameScreen.getClient().sendToAll(new DropItem(E(player).networkId(), GUI.getInventory().selectedIndex(), E(player).getWorldPos()));
        });
    }

    private void toggleDialogText() {
        if (GUI.getDialog().isVisible()) {
            String message = GUI.getDialog().getMessage();
            GameScreen.getClient().sendToAll(new TalkRequest(message));
        }
        GUI.getDialog().toggle();
        E(GameScreen.getPlayer()).writing(GUI.getDialog().isVisible());
    }

    private void toggleMeditate() {
        GameScreen.getClient().sendToAll(new MeditateRequest());
    }

    private void toggleInventory() {
        GUI.getInventory().setVisible(!GUI.getInventory().isVisible());
    }

}
