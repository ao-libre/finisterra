package game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Tooltip;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.esotericsoftware.minlog.Log;
import entity.character.equipment.Shield;
import game.handlers.ObjectHandler;
import game.utils.Skins;
import game.utils.WorldUtils;
import shared.objects.types.*;

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
        tooltip = getTooltip( item );
        addListener( tooltip );
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
        ObjectHandler objectHandler = WorldUtils.getWorld().orElse(null).getSystem(ObjectHandler.class);
        Optional<Obj> object = objectHandler.getObject(getObjId());
        object.ifPresent(obj -> {
            TextureRegion graphic = objectHandler.getGraphic(obj);
            batch.draw(graphic, getX() + 1, getY() + 1);
        });
    }

    void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Tooltip< Actor > getTooltip(Item item) {
        int objID = item.objId;
        ObjectHandler objectHandler = WorldUtils.getWorld().orElse(null).getSystem(ObjectHandler.class);
        Optional<Obj> obj = objectHandler.getObject(objID);
        Actor content  = createTooltipContent ( obj.get() );
        return new Tooltip<>(content);
    }
    private Actor createTooltipContent(Obj obj) {
        String name = obj.getName();
        Type objType = obj.getType ();

        Table table = new Table();
        table.pad(0, 10, 10, 0);
        table.setSkin( Skins.COMODORE_SKIN );
        table.background( getBackground() );
        Label title = new Label(" - " + name + " - ", Skins.COMODORE_SKIN, "title-no-background");
        Label type = new Label(objType.toString () , Skins.COMODORE_SKIN, "desc-no-background");
        table.add("").prefWidth( 200 ).fillX().row();
        table.add(title).center().row();
        table.add(type).center().row();
        table.add("").row();

        switch (objType){
            case WEAPON:
                WeaponObj weaponObj = (WeaponObj) obj;
                table.add("kind = " + weaponObj.getKind ().toString ()).left().row();
                table.add("Max hit = " + weaponObj.getMaxHit ()).left().row();
                table.add("Min hit = " + weaponObj.getMinHit ()).left().row();
                break;
            case POTION:
                PotionObj potionObj = (PotionObj) obj;
                table.add("Kind = " +potionObj.getKind ().toString ()).left().row();
                table.add("Max = " + potionObj.getMax ()).left().row();
                table.add("Min = " + potionObj.getMin ()).left().row();
                table.add("EffectTime = " + potionObj.getEffecTime ()).left().row();
                break;
            case ARMOR:
                ArmorObj armorObj = (ArmorObj) obj;
                table.add("Max Def = " + armorObj.getMaxDef()).left().row();
                table.add("Min Def = " + armorObj.getMinDef()).left().row();
                break;
            case HELMET:
                HelmetObj helmetObj = (HelmetObj) obj;
                table.add("Max Def = " + helmetObj.getMaxDef()).left().row();
                table.add("Min Def = " + helmetObj.getMinDef()).left().row();
                break;
            case ARROW:
                ArrowObj arrowObj = (ArrowObj) obj;
                table.add("Max Hit = " + arrowObj.getMaxHit()).left().row();
                table.add("Min Hit = " + arrowObj.getMinHit()).left().row();
                break;
            case SHIELD:
                ShieldObj shieldObj = (ShieldObj) obj;
                table.add("Max Def = " + shieldObj.getMaxDef()).left().row();
                table.add("Min Def = " + shieldObj.getMinDef()).left().row();

                break;
        }
        table.add("Value = " + obj.getValue()).left().row();
        table.add("Count = " + item.map(item1 -> item1.count).orElse(1) ).left().row();
        table.add("").row();
        return table;
    }
}
