package entity.character.info;

import com.artemis.Component;

import java.util.Optional;
import java.util.stream.Stream;

public class Inventory extends Component {
    private final static int SIZE = 6;
    public Item[] items = new Item[SIZE];
    public Item[] shortcuts = new Item[4];

    public Inventory() {
    }

    public void set(int i, Item item) {
        items[i] =  item;
    }

    public void add(int objId) {
        add(objId,1);
    }

    public int add(int objId, int count) {
        for (int i = 0; i < 20; i++) {
            if (items[i] == null) {
                items[i] = new Item(objId, count);
                return i;
            } else if (items[i].objId == objId) {
                items[i].count += count;
                return i;
            }
        }
        return -1;
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

    public void addShortcut(int shorcut, int realSlot) {
        shortcuts[shorcut] = items[realSlot];
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

    }
}
