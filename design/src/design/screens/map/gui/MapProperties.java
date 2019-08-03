package design.screens.map.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Window;
import design.editors.fields.BooleanEditor;
import design.editors.fields.IntegerEditor;
import design.editors.fields.StringEditor;
import shared.model.map.Map;

import static launcher.DesignCenter.SKIN;

public class MapProperties extends Window {

    private Map current;

    public MapProperties() {
        super("Map Properties", SKIN, "main");
        setMovable(true);
        defaults().growX().space(3);
    }

    public void show(Map map) {
        clear();
        current = map;
        add(StringEditor.simple("Map Name", map::setName, map::getName, () -> {})).row();
        int[] neighbours = map.getNeighbours();
        for (int i = 0; i < 4; i++) {
            int finalI = i;
            add(IntegerEditor.create(getNeighbourDisplay(i), id -> map.setNeighbour(finalI, id), () -> neighbours[finalI], () -> {
            })).row();
        }
        add(BooleanEditor.simple("Secure", map::setSecureZone, () -> current.isSecureZone(), () -> { })).row();
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
}
