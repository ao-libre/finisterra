package design.screens.map.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Window;
import design.editors.fields.FieldProvider;
import design.editors.fields.IntegerEditor;
import shared.model.map.WorldPosition;

import static launcher.DesignCenter.SKIN;

public class MapAssetChooser extends Window {

    private int image;
    private int animation;
    private int tileset;
    private WorldPosition tileExit = new WorldPosition();

    public MapAssetChooser() {
        super("Chooser", SKIN, "main");
        defaults().growX();
        setDebug(true);
        add("Image:").row();
        add(IntegerEditor.create("", FieldProvider.IMAGE, i -> image = i, () -> image, () -> {
        })).spaceLeft(2).row();
        add("Animation:").row();
        add(IntegerEditor.create("", FieldProvider.ANIMATION, i -> animation = i, () -> animation, () -> {
        })).spaceLeft(2).row();

        add("Tile Exit: ").row();
        add(IntegerEditor.create("Map", tileExit::setMap, tileExit::getMap, () -> {
        })).spaceLeft(2).row();
        add(IntegerEditor.create("X", tileExit::setY, tileExit::getX, () -> {
        })).spaceLeft(2).row();
        add(IntegerEditor.create("Y", tileExit::setY, tileExit::getY, () -> {
        })).spaceLeft(2).row();
    }

    public int getAnimation() {
        return animation;
    }

    public int getImage() {
        return image;
    }

    public int getTileset() {
        return tileset;
    }

    public WorldPosition getTileExit() {
        return tileExit;
    }
}
