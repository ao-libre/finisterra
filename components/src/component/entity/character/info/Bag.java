package component.entity.character.info;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

import java.util.Optional;
import java.util.stream.Stream;

@PooledWeaver
public class Bag extends Component {
    public final static int SIZE = 20;
    public Item[] items = new Item[SIZE];

    public Bag() {
    }

    public Item[] getItems() {
        return items;
    }

    public void setItems(Item[] items) {
        this.items = items;
    }

    public void set(int i, Item item) {
        items[i] = item;
    }

    public void add(int objId, boolean equipped) {
        add(objId, 1, equipped);
    }

    public int add(int objId, int count, boolean equiped) {
        for (int i = 0; i < 20; i++) {
            if (items[i] == null) {
                items[i] = new Item(objId, count, equiped);
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

    public Item[] userItems() {
        return items;
    }

    public static class Item {
        public int count;
        public int objId;
        public boolean equipped;

        public Item() {
        }

        public Item(int objId, int count, boolean equipped) {
            this.objId = objId;
            this.count = count;
            this.equipped = equipped;
        }

        public Item(int objId, int count) {
            this(objId, count, false);
        }

    }
}
