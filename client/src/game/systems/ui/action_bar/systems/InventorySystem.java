package game.systems.ui.action_bar.systems;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Tooltip;
import component.entity.character.info.Bag;
import component.position.WorldPos;
import game.handlers.DefaultAOAssetManager;
import game.systems.PlayerSystem;
import game.systems.resources.MapSystem;
import shared.model.map.Tile;
import shared.systems.IntervalSystem;
import game.systems.actions.PlayerActionSystem;
import game.systems.network.ClientSystem;
import game.systems.resources.ObjectSystem;
import game.systems.ui.UserInterfaceContributionSystem;
import game.systems.ui.UserInterfaceSystem;
import game.ui.Inventory;
import game.utils.Skins;
import shared.network.interaction.DropItem;
import shared.network.interaction.TakeItemRequest;
import shared.network.inventory.InventoryUpdate;
import shared.objects.types.*;
import shared.systems.IntervalSystem;
import shared.util.ItemUtils;
import shared.util.Messages;

import java.util.Optional;

import static com.artemis.E.E;

@Wire
public class InventorySystem extends UserInterfaceContributionSystem {

    private ClientSystem clientSystem;
    private PlayerSystem playerSystem;
    private ObjectSystem objectSystem;
    private UserInterfaceSystem userInterfaceSystem;
    private PlayerActionSystem playerActionSystem;
    private IntervalSystem intervalSystem;
    @Wire
    private DefaultAOAssetManager assetManager;
    private MapSystem mapSystem;

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
                Vector2 screenCoordinates = localToScreenCoordinates(new Vector2(x, y));
                dropItem(i, getWorldPos(screenCoordinates.x, screenCoordinates.y));
            }

            @Override
            protected void swap(int originIndex, int targetIndex) {
                InventorySystem.this.swap(originIndex, targetIndex);
            }

            @Override
            protected TextureRegion getGraphic(Bag.Item item) {
                return InventorySystem.this.getGraphic(item.objId);
            }

            @Override
            protected Tooltip getTooltip(Bag.Item item) {
                Optional<Obj> obj = objectSystem.getObject(item.objId);
                Actor content = obj.map(o -> createTooltipContent(item, o)).orElse(null);
                return new Tooltip<>(content);
            }


            private Actor createTooltipContent(Bag.Item item, Obj obj) {
                String name = obj.getName();
                Type objType = obj.getType();

                Table table = new Table();
                table.pad(0, 10, 10, 0);
                table.setSkin(Skins.COMODORE_SKIN);
                table.background(getBackground());
                Label title = new Label(" - " + name + " - ", Skins.COMODORE_SKIN, "title-no-background");
                Label type = new Label(objType.toString(), Skins.COMODORE_SKIN, "desc-no-background");
                table.add("").prefWidth(200).fillX().row();
                table.add(title).center().row();
                table.add(type).center().row();
                table.add("").row();

                switch (objType) {
                    case WEAPON:
                        WeaponObj weaponObj = (WeaponObj) obj;
                        WeaponKind weaponKind = weaponObj.getKind();
                        switch (weaponKind) {
                            case AXE:
                                table.add(assetManager.getMessages(Messages.OBJ_kIND)
                                        + assetManager.getMessages(Messages.WEAPON_AXE)).left().row();
                                break;
                            case BOW:
                                table.add(assetManager.getMessages(Messages.OBJ_kIND)
                                        + assetManager.getMessages(Messages.WEAPON_BOW)).left().row();
                                break;
                            case DAGGER:
                                table.add(assetManager.getMessages(Messages.OBJ_kIND)
                                        + assetManager.getMessages(Messages.WEAPON_DAGGER)).left().row();
                                break;
                            case MACE:
                                table.add(assetManager.getMessages(Messages.OBJ_kIND)
                                        + assetManager.getMessages(Messages.WEAPON_MACE)).left().row();
                                break;
                            case POLE:
                                table.add(assetManager.getMessages(Messages.OBJ_kIND)
                                        + assetManager.getMessages(Messages.WEAPON_POLE)).left().row();
                                break;
                            case WORK:
                                table.add(assetManager.getMessages(Messages.OBJ_kIND)
                                        + assetManager.getMessages(Messages.WEAPON_WORK)).left().row();
                                break;
                            case STAFF:
                                table.add(assetManager.getMessages(Messages.OBJ_kIND)
                                        + assetManager.getMessages(Messages.WEAPON_STAFF)).left().row();
                                break;
                            case SWORD:
                                table.add(assetManager.getMessages(Messages.OBJ_kIND)
                                        + assetManager.getMessages(Messages.WEAPON_SWORD)).left().row();
                                break;
                        }
                        table.add(assetManager.getMessages(Messages.OBJ_MAXHIT) + weaponObj.getMaxHit()).left().row();
                        table.add(assetManager.getMessages(Messages.OBJ_MINHIT) + weaponObj.getMinHit()).left().row();
                        break;
                    case POTION:
                        PotionObj potionObj = (PotionObj) obj;
                        table.add(assetManager.getMessages(Messages.OBJ_kIND) + potionObj.getKind().toString()).left().row();
                        table.add(assetManager.getMessages(Messages.POTION_Max) + potionObj.getMax()).left().row();
                        table.add(assetManager.getMessages(Messages.POTION_MIN) + potionObj.getMin()).left().row();
                        table.add(assetManager.getMessages(Messages.POTION_ET) + potionObj.getEffecTime()).left().row();
                        break;
                    case ARMOR:
                        ArmorObj armorObj = (ArmorObj) obj;
                        table.add(assetManager.getMessages(Messages.OBJ_MAXDEF) + armorObj.getMaxDef()).left().row();
                        table.add(assetManager.getMessages(Messages.OBJ_MINDEF) + armorObj.getMinDef()).left().row();
                        break;
                    case HELMET:
                        HelmetObj helmetObj = (HelmetObj) obj;
                        table.add(assetManager.getMessages(Messages.OBJ_MAXDEF) + helmetObj.getMaxDef()).left().row();
                        table.add(assetManager.getMessages(Messages.OBJ_MINDEF) + helmetObj.getMinDef()).left().row();
                        break;
                    case ARROW:
                        ArrowObj arrowObj = (ArrowObj) obj;
                        table.add(assetManager.getMessages(Messages.OBJ_MAXHIT) + arrowObj.getMaxHit()).left().row();
                        table.add(assetManager.getMessages(Messages.OBJ_MINHIT) + arrowObj.getMinHit()).left().row();
                        break;
                    case SHIELD:
                        ShieldObj shieldObj = (ShieldObj) obj;
                        table.add(assetManager.getMessages(Messages.OBJ_MAXDEF) + shieldObj.getMaxDef()).left().row();
                        table.add(assetManager.getMessages(Messages.OBJ_MAXDEF) + shieldObj.getMinDef()).left().row();

                        break;
                }
                table.add(assetManager.getMessages(Messages.OBJ_VALUE) + obj.getValue()).left().row();
                table.add(assetManager.getMessages(Messages.OBJ_COUNT) + item.count)
                        .left().row();
                table.add("").row();
                return table;
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
        getSelectedObject().ifPresent(obj -> {
            if (ItemUtils.canEquip(obj)) {
                playerActionSystem.equipItem(getSelectedIndex());
            }
        });
    }

    public void use() {
        getSelectedObject().ifPresent(obj -> {
            if (ItemUtils.canUse(obj)) {
                playerActionSystem.useItem(getSelectedIndex());
            }
        });

    }

    public void show() {
        inventory.setVisible(true);
    }

    public void hide() {
        inventory.setVisible(false);
    }

    private Optional<Obj> getSelectedObject() {
        Optional<Bag.Item> selected = Optional.ofNullable(getSelected());
        return selected.flatMap(item -> getObject(item.objId));
    }

    private Optional<Obj> getObject(int objId) {
        return objectSystem.getObject(objId);
    }

    public TextureRegion getGraphic(int objId) {
        return objectSystem.getObject(objId).map(obj -> objectSystem.getGraphic(obj)).orElse(null);
    }

    private void swap(int originIndex, int targetIndex) {
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

    private <T> void swap(T[] a, int i, int j) {
        T t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    public Optional<WorldPos> getWorldPos(float x, float y) {
        // TODO handle valid position to avoid droping items over npcs in citys
        WorldPos dropWorldPos = userInterfaceSystem.getWorldPos((int) x, (int) y);
        Tile tile = mapSystem.getTile(  dropWorldPos);
        if (!tile.isBlocked()) {
            return Optional.of( userInterfaceSystem.getWorldPos( (int) x, (int) y ) );
        } else {
            return Optional.of( playerSystem.getWorldPos() );
        }

    }

    public void update(Bag bag) {
        inventory.update(bag);
    }

    public void toggleExpanded(){
        inventory.toggleExpanded(playerSystem.get().getBag());
    }

}
