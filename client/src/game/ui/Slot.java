package game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Tooltip;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import game.AOGame;
import game.handlers.AOAssetManager;
import game.systems.resources.ObjectSystem;
import game.utils.Skins;
import game.utils.WorldUtils;
import shared.objects.types.*;
import shared.util.Messages;

import java.util.Optional;

import static entity.character.info.Inventory.Item;

public class Slot extends ImageButton {

    static final int SIZE = 64;

    private static Drawable selection = Skins.COMODORE_SKIN.getDrawable("slot-selected2");
    private static Texture equip = new Texture(Gdx.files.local("data/ui/images/slot-equipped.png"));

    private Optional<Item> item = Optional.empty();

    private boolean selected;
    private Tooltip tooltip;

    Slot() {
        super(Skins.COMODORE_SKIN, "icon-container");
    }

    public Slot(Item item) {
        this();
        this.item = Optional.of(item);
    }

    private int getObjId() {
        return item.map(item1 -> item1.objId).orElse(-1);
    }

    public int getCount() {
        return item.map(item1 -> item1.count).orElse(0);
    }

    Optional<Item> getItem() {
        return item;
    }

    void setItem(Item item) {
        this.item = Optional.ofNullable(item);
        if (item == null) {
            return;
        }
        if (tooltip != null) {
            removeListener(tooltip);
        }
        tooltip = getTooltip(item);
        addListener(tooltip);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (item.isPresent()) {
            drawItem(batch);
            if (item.get().equipped) {
                batch.draw(equip, getX(), getY(), SIZE, SIZE);
            }
            if (selected) {
                selection.draw(batch, getX(), getY(), SIZE, SIZE);
            }
        }
    }

    private void drawItem(Batch batch) {
        ObjectSystem objectSystem = WorldUtils.getWorld().orElse(null).getSystem(ObjectSystem.class);
        Optional<Obj> object = objectSystem.getObject(getObjId());
        object.ifPresent(obj -> {
            TextureRegion graphic = objectSystem.getGraphic(obj);
            batch.draw(graphic, getX() + 1, getY() + 1);
        });
    }

    void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Tooltip<Actor> getTooltip(Item item) {
        int objID = item.objId;
        ObjectSystem objectSystem = WorldUtils.getWorld().orElse(null).getSystem(ObjectSystem.class);
        Optional<Obj> obj = objectSystem.getObject(objID);
        Actor content = createTooltipContent(obj.get());
        return new Tooltip<>(content);
    }

    private Actor createTooltipContent(Obj obj) {
        AOAssetManager assetManager = AOGame.getGlobalAssetManager();
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
        table.add(assetManager.getMessages(Messages.OBJ_COUNT) + item.map(item1 -> item1.count).orElse(1))
                .left().row();
        table.add("").row();
        return table;
    }
}
