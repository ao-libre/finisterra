package design.screens.map.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import design.editors.fields.FieldProvider;
import design.editors.fields.IntegerEditor;
import design.screens.map.gui.MapPalette.Selection;
import shared.model.map.WorldPosition;

import static launcher.DesignCenter.SKIN;

public class MapAssetChooser extends Window implements MapPalette.SelectionListener {

    private final ClickListener mouseListener;
    private final WorldPosition tileExit = new WorldPosition();
    private int image;
    private int animation;
    private int tileset;

    public MapAssetChooser() {
        super("Chooser", SKIN, "main");
        addListener(mouseListener = new ClickListener());
        defaults().growX();

        selectionChange(Selection.NONE, 0);
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

    public boolean isOver() {
        return mouseListener.isOver();
    }

    @Override
    public void selectionChange(Selection selection, int layer) {
        clear();
        switch (selection) {
            case CLEAN:
            case BLOCK:
                setVisible(false);
                break;

            case TILE_EXIT:
                setVisible(true);
                showTileExit();
                break;
            case NONE:
                setVisible(true);
                switch (layer) {
                    case 0:
                    case 3:
                        showImage();
                        break;
                    case 2:
                        showImage();
                    case 1:
                        showAnimation();
                        break;
                }
                break;
            case TILE_SET:
                setVisible(true);
                showTileSet();
                break;
        }
    }

    private void showTileSet() {
        add("Tile set:").row();
        add(IntegerEditor.create("", FieldProvider.TILE_SET, i -> tileset = i, () -> tileset, () -> {
        })).spaceLeft(2).row();
    }

    private void showAnimation() {
        add("Animation:").row();
        add(IntegerEditor.create("", FieldProvider.ANIMATION, i -> animation = i, () -> animation, () -> {
        })).spaceLeft(2).row();
    }

    private void showImage() {
        add("Image:").row();
        add(IntegerEditor.create("", FieldProvider.IMAGE, i -> image = i, () -> image, () -> {
        })).spaceLeft(2).row();
    }

    private void showTileExit() {
        add("Tile Exit: ").row();
        add(IntegerEditor.create("Map", tileExit::setMap, tileExit::getMap, () -> {
        })).spaceLeft(2).row();
        add(IntegerEditor.create("X", tileExit::setY, tileExit::getX, () -> {
        })).spaceLeft(2).row();
        add(IntegerEditor.create("Y", tileExit::setY, tileExit::getY, () -> {
        })).spaceLeft(2).row();
    }
}
