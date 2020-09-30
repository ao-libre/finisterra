package game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Tooltip;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import game.utils.Skins;

import java.util.Optional;

import static component.entity.character.info.Bag.Item;

public class Slot extends ImageButton {

    static final int SIZE = 48;

    private static Drawable selection = WidgetFactory.createDrawable(WidgetFactory.Drawables.INVENTORY_SLOT_SELECTION.name);
    private static Drawable overlay = WidgetFactory.createDrawable(WidgetFactory.Drawables.INVENTORY_SLOT_OVERLAY.name);
    private static Texture equip = new Texture(Gdx.files.local("data/ui/images/slot-equipped.png"));

    private Optional<Item> item = Optional.empty();

    private boolean dragging;
    private boolean selected;
    private Tooltip tooltip;
    private TextureRegion graphic;

    Slot() {
        super(Skins.CURRENT.get(), "slot");
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

    public TextureRegion getGraphic() {
        return graphic;
    }

    void setItem(Item item, TextureRegion graphic, Tooltip tooltip) {
        this.item = Optional.ofNullable(item);
        if (item == null) {
            return;
        }
        this.graphic = graphic;
        if (this.tooltip != null) {
            removeListener(tooltip);
        }
        this.tooltip = tooltip;
        addListener(tooltip);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (item.isPresent()) {
            if (!dragging) drawItem(batch);
            if (item.get().equipped) {
                batch.draw(equip, getX(), getY(), getWidth(), getHeight());
            }

            if (selected) {
                selection.draw(batch, getX(), getY(), getWidth(), getHeight());
            }
        }
        overlay.draw(batch, getX(), getY(), getWidth(), getHeight());
    }

    private void drawItem(Batch batch) {
        if (graphic != null) {
            batch.draw(graphic, getX() + 3, getY() + 3, getWidth() - 6, getHeight() - 6);
        }
    }

    void setSelected(boolean selected) {
        this.selected = selected;
    }

    void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

}
