package entity.character.info;

import com.artemis.Component;

import java.util.ArrayList;
import java.util.Optional;

public class Inventory extends Component {
    public ArrayList<Item> items = new ArrayList<>();

    public Inventory(){}

    public void add(int objId) {
        add(objId, 1);
    }

    public void add(int objId, int count) {
        items.add(new Item(objId, count));
    }

    public void remove(int objId) {
        remove(objId, 1);
    }

    public void remove(int objId, int count) {
        Optional<Item> item = items.stream().filter(it -> it.objId == objId).findFirst();
        item.ifPresent(it -> {
            if (it.count <= count) {
                items.remove(it);
            } else {
                it.count -= count;
            }
        });
    }

    public ArrayList<Item> userItems() {
        return items;
    }

    public static class Item {
        public int count;
        public int objId;

        public Item(int objId, int count) {
            this.objId = objId;
            this.count = count;
        }
    }
}
