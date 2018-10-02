package ar.com.tamborindeguy.client.systems.interactions;

import ar.com.tamborindeguy.client.handlers.ObjectHandler;
import ar.com.tamborindeguy.objects.types.Obj;
import camera.Focused;
import com.artemis.Aspect;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import entity.character.info.Inventory;

import java.util.*;

import static com.artemis.E.E;

public class InventorySystem extends IteratingSystem {

    private Window inventory;
    private Map<Inventory.Item, Actor> items = new HashMap<>();

    public InventorySystem(Window inventory) {
        super(Aspect.all(Focused.class, Inventory.class));
        this.inventory = inventory;
    }

    @Override
    protected void process(int entityId) {
        Inventory userInventory = E(entityId).getInventory();
        ArrayList<Inventory.Item> userItems = userInventory.userItems();
        // add new items
        userItems
                .stream()
                .filter(item -> !items.containsKey(item))
                .forEach(item -> {
                    Optional<Obj> object = ObjectHandler.getObject(item.objId);
                    object.ifPresent(obj -> {
                        Image image = new Image(ObjectHandler.getGraphic(obj));
                        items.put(item, image);
                        inventory.add(image);
                    });
                });
        // remove not present
        items
                .keySet()
                .stream()
                .filter(item -> !userItems.contains(item))
                .forEach(item -> {
                    Actor actor = items.get(item);
                    items.remove(item);
                    inventory.removeActor(actor);
                });
    }
}
