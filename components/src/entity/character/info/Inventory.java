package entity.character.info;

import com.artemis.Component;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

public class Inventory extends Component {
    public Item[] items = new Item[20];

    public Inventory() {
    }

    public void set(int i, Item item) {
        items[i] =  item;
    }

    public void add(int objId) {
        add(objId,1);
    }

    public void add(int objId, int count) {
        for (int i = 0; i < 20; i++) {
            if (items[i] == null) {
                items[i] = new Item(objId, count);
                return;
            } else if (items[i].objId == objId) {
                items[i].count += count;
                return;
            }
        }
        // TODO notify no space
    }

    public void remove(int position) {
        items[position] = null;
    }

    public void remove(int objId, int count) {
        Optional<Item> item = Stream.of(items).filter(it -> it.objId == objId).findFirst();
        item.ifPresent(it -> {
            it.count -= count;
        });
    }

    public Item[] userItems() {
        return items;
    }

    public static class Item {
        public int count;
        public int objId;
        public boolean equipped;

        public Item(){}
        public Item(int objId, int count) {
            this.objId = objId;
            this.count = count;
        }

        public void action() {
            equipped = !equipped;
        }
    }
}
