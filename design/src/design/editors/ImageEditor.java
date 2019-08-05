package design.editors;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import design.editors.fields.IntegerEditor;
import model.textures.AOImage;
import org.jetbrains.annotations.NotNull;

import static launcher.DesignCenter.SKIN;

public class ImageEditor extends Dialog {

    private AOImage image;

    public ImageEditor(AOImage image) {
        super("Image Editor", SKIN);
        this.image = image;
        addTable();
        button("Cancel", false);
        button("OK", this.image);
    }

    @NotNull
    public Table getTable(AOImage image) {
        Table table = new Table(SKIN);
        table.pad(20);
        table.defaults().growX().uniform();

        table.add(new Label("ID: " + image.getId(), SKIN)).row();
        table.add(IntegerEditor.create("Y", image::setY, image::getY, () -> {
        })).row();
        table.add(IntegerEditor.create("X", image::setX, image::getX, () -> {
        })).row();
        table.add(IntegerEditor.create("Width", image::setWidth, image::getWidth, () -> {
        })).row();
        table.add(IntegerEditor.create("Height", image::setHeight, image::getHeight, () -> {
        })).row();
        table.add(IntegerEditor.create("File", image::setFileNum, image::getFileNum, () -> {
        })).row();
        return table;
    }

    private void addTable() {
        getContentTable().add(new ScrollPane(getTable(image))).prefHeight(300).prefWidth(300);
    }

}
