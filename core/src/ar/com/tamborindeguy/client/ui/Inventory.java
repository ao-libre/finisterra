package ar.com.tamborindeguy.client.ui;

import ar.com.tamborindeguy.client.game.AO;
import ar.com.tamborindeguy.client.utils.Skins;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

import java.util.ArrayList;

import static com.artemis.E.E;

public class Inventory extends Window {

    private static final int SIZE = 36;
    public static final int COLUMNS = 6;

    private ArrayList<Slot> slots;

    public Inventory() {
        super("Inventory", Skins.COMODORE_SKIN, "black");
        slots = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            slots.add(new Slot());
        }
    }

    public void fillUserInventory(int player) {
        ArrayList<entity.character.info.Inventory.Item> userItems = E(player).getInventory().items;
        userItems.forEach(item -> {
            if (item != null) {
                slots.set(userItems.indexOf(item), new Slot(item.objId, item.count));
            }
        });
        pad(15);
        for(int i = 0; i < SIZE; i++) {
            add(slots.get(i)).width(Slot.SIZE * AO.GAME_SCREEN_ZOOM).height(Slot.SIZE * AO.GAME_SCREEN_ZOOM);
            if (i > 0 && (i-1) % COLUMNS == 0) {
                row();
            }
        }
    }

}
