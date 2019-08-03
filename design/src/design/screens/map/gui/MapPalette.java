package design.screens.map.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Window;
import design.editors.fields.FieldProvider;
import design.editors.fields.IntegerEditor;

import static launcher.DesignCenter.SKIN;

public class MapPalette extends Window {

    private int image;
    private int animation;

    public MapPalette() {
        super("Palette", SKIN, "main");
        defaults().pad(20);
        add(IntegerEditor.create("Image", FieldProvider.IMAGE, i -> image = i, () -> image, () -> {
        })).row();
        add(IntegerEditor.create("Animation", FieldProvider.ANIMATION, i -> animation = i, () -> animation, () -> {
        })).row();
    }

    public int getAnimation() {
        return animation;
    }

    public int getImage() {
        return image;
    }
}
