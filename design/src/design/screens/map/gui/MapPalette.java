package design.screens.map.gui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;
import java.util.List;

import static launcher.DesignCenter.SKIN;

public class MapPalette extends Window {

    private final ClickListener mouseListener;
    // palette
    private int layer;
    private Selection selection = Selection.NONE;
    private List<SelectionListener> listeners = new ArrayList<>();

    public MapPalette() {
        super("Palette", SKIN, "main");
        addListener(mouseListener = new ClickListener());
        setMovable(true);

        ButtonGroup layers = new ButtonGroup();
        Button fst = new TextButton("1", SKIN, "file");
        fst.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                layer = 0;
                notifySelection(selection);
            }
        });
        add(fst).growX().row();
        Button snd = new TextButton("2", SKIN, "file");
        snd.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                layer = 1;
                notifySelection(selection);
            }
        });
        add(snd).growX().row();
        Button third = new TextButton("3", SKIN, "file");
        third.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                layer = 2;
                notifySelection(selection);
            }
        });
        add(third).growX().row();
        Button forth = new TextButton("4", SKIN, "file");
        forth.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                layer = 3;
                notifySelection(selection);
            }
        });
        add(forth).growX().row();
        layers.add(fst, snd, third, forth);

        defaults().space(10);
        Button block = new TextButton("block", SKIN, "file");
        block.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selection = block.isChecked() ? Selection.BLOCK : Selection.NONE;
                notifySelection(selection);
            }
        });
        add(block).row();
        Button clean = new Button(SKIN, "delete-check");
        clean.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selection = clean.isChecked() ? Selection.CLEAN : Selection.NONE;
                notifySelection(selection);
            }
        });
        add(clean).row();

        Button tileExit = new TextButton("Tile Exit", SKIN, "file");
        tileExit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selection = tileExit.isChecked() ? Selection.TILE_EXIT : Selection.NONE;
                notifySelection(selection);
            }
        });
        add(tileExit).row();

        Button tileSet = new TextButton("Tile Set", SKIN, "file");
        tileSet.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selection = tileSet.isChecked() ? Selection.TILE_SET : Selection.NONE;
                notifySelection(selection);
            }
        });
        add(tileSet).row();


        ButtonGroup buttons = new ButtonGroup();
        buttons.add(block, clean, tileExit, tileSet);
        buttons.setMinCheckCount(0);

    }

    public void addListener(SelectionListener listener) {
        listeners.add(listener);
    }

    private void notifySelection(Selection selection) {
        listeners.forEach(listener -> listener.selectionChange(selection, layer));
    }

    public int getLayer() {
        return layer;
    }

    public Selection getSelection() {
        return selection;
    }

    public boolean isOver() {
        return mouseListener.isOver();
    }

    public enum Selection {
        NONE,
        BLOCK,
        CLEAN,
        TILE_EXIT,
        TILE_SET
    }

    public interface SelectionListener {

        void selectionChange(Selection selection, int layer);
    }
}
