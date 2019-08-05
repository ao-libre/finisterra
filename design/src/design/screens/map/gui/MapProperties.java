package design.screens.map.gui;

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import design.editors.fields.BooleanEditor;
import design.editors.fields.IntegerEditor;
import design.editors.fields.StringEditor;
import shared.model.map.Map;

import static launcher.DesignCenter.SKIN;

public class MapProperties extends Window {

    private Map current;
    private ClickListener mouseListener;
    private Table content;

    public MapProperties() {
        super("Map Properties", SKIN, "main");
        setMovable(true);
        defaults().growX().space(3);
        addListener(mouseListener = new ClickListener());
        content = new Table();
        content.defaults().growX().space(3);
        add(new ScrollPane(content)).growX();
    }

    public void show(Map map) {
        content.clear();
        current = map;
        content.add(StringEditor.simple("Map Name", map::setName, map::getName, () -> {
        })).row();
        int[] neighbours = map.getNeighbours();
        for (int i = 0; i < 4; i++) {
            int finalI = i;
            content.add(IntegerEditor.create(getNeighbourDisplay(i), id -> map.setNeighbour(finalI, id), () -> neighbours[finalI], () -> {
            })).row();
        }
        content.add(BooleanEditor.simple("Secure", map::setSecureZone, () -> current.isSecureZone(), () -> {
        })).row();
    }

    private String getNeighbourDisplay(int i) {
        String label = "";
        switch (i) {
            case 0:
                label = "Left";
                break;
            case 1:
                label = "Top";
                break;
            case 2:
                label = "Right";
                break;
            case 3:
                label = "Down";
        }
        return label + " Map";
    }

    public Map getCurrent() {
        return current;
    }

    public boolean isOver() {
        return mouseListener.isOver();
    }
}
