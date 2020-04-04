package game.systems.ui.action_bar.systems;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import component.entity.character.info.Bag;
import game.systems.PlayerSystem;
import game.systems.network.ClientSystem;
import game.systems.resources.ObjectSystem;
import game.systems.ui.UserInterfaceContributionSystem;
import game.systems.ui.UserInterfaceSystem;
import game.ui.Inventory;
import component.position.WorldPos;
import shared.network.interaction.DropItem;
import shared.network.interaction.TakeItemRequest;
import shared.network.inventory.InventoryUpdate;
import shared.network.inventory.ItemActionRequest;
import shared.objects.types.Obj;

import java.util.Optional;

import static com.artemis.E.E;

@Wire
public class InventorySystem extends UserInterfaceContributionSystem {

    private ClientSystem clientSystem;
    private PlayerSystem playerSystem;
    private ObjectSystem objectSystem;
    private UserInterfaceSystem userInterfaceSystem;

    private Inventory inventory;

    public InventorySystem() {
        super(Aspect.all(Bag.class));
    }

    @Override
    public void calculate(int entityId) {
        inventory = new Inventory() {

            @Override
            protected void doubleClick() {
                use();
            }

            @Override
            protected void dragAndDropOut(int i, int x, int y) {
                dropItem(i, getWorldPos(x, y));
            }

            @Override
            protected void swap(int originIndex, int targetIndex) {
                InventorySystem.this.swap(originIndex, targetIndex);
            }

            @Override
            protected TextureRegion getGraphic(Bag.Item item) {
                return InventorySystem.this.getGraphic(item.objId);
            }
        };
        inventory.update(E(entityId).getBag());
    }

    @Override
    public Actor getActor() {
        return inventory;
    }

    @Override
    protected void processSystem() {
        // check if inventory changed and recalculate
    }

    public Bag.Item getSelected() {
        return playerSystem.get().bagItems()[inventory.selectedIndex()];
    }

    public int getSelectedIndex() {
        return inventory.selectedIndex();
    }

    public void dropItem() {
        dropItem(getSelectedIndex(), Optional.empty());
    }

    public void dropItem(Optional<WorldPos> pos) {
        dropItem(getSelectedIndex(), pos);
    }

    public void dropItem(int droppingIndex, Optional<WorldPos> pos) {
        clientSystem.send(new DropItem(droppingIndex, pos.orElse(playerSystem.getWorldPos())));
    }

    public void takeItem() {
        clientSystem.send(new TakeItemRequest());
    }

    public void equip() {
        clientSystem.send(new ItemActionRequest(getSelectedIndex()));
    }

    public void use() {
        // TODO handle usable items
        clientSystem.send(new ItemActionRequest(getSelectedIndex()));
    }

    public void show() {
        inventory.setVisible(true);
    }

    public void hide() {
        inventory.setVisible(false);
    }

    public Optional<Obj> getObject(int objId) {
        return objectSystem.getObject(objId);
    }

    public TextureRegion getGraphic(int objId) {
        return objectSystem.getObject(objId).map(obj -> objectSystem.getGraphic(obj)).orElse(null);
    }

    public void swap(int originIndex, int targetIndex) {
        E playerEntity = playerSystem.get();
        InventoryUpdate update = new InventoryUpdate(playerEntity.getNetwork().id);
        Bag.Item[] userItems = playerEntity.bagItems();
        Bag.Item originItem = userItems[originIndex];
        if (userItems[targetIndex] != null) {
            update.add(targetIndex, originItem);
            update.add(originIndex, userItems[targetIndex]);
            swap(userItems, originIndex, targetIndex);
        } else {
            update.add(targetIndex, originItem);
            update.remove(originIndex);
            userItems[targetIndex] = originItem;
            userItems[originIndex] = null;
        }
        clientSystem.send(update);
        inventory.update(playerEntity.getBag());
    }

    final <T> void swap(T[] a, int i, int j) {
        T t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    public Optional<WorldPos> getWorldPos(int x, int y) {
        // TODO handle valid component.position to avoid droping items over blocks
        return Optional.of(userInterfaceSystem.getWorldPos(x, y));
    }

    public void update(Bag bag) {
        inventory.update(bag);
    }
}
