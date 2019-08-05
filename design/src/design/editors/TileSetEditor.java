package design.editors;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import design.editors.fields.FieldEditor.FieldListener;
import design.editors.fields.IntegerEditor;
import design.screens.map.model.TileSet;
import org.jetbrains.annotations.NotNull;

import static launcher.DesignCenter.SKIN;

public class TileSetEditor extends Dialog {

    private TileSet tileSet;

    public TileSetEditor(TileSet tileSet) {
        super("Tile Set Editor", SKIN);
        this.tileSet = tileSet;
        addTable();
        button("Cancel", false);
        button("OK", tileSet);
    }

    @NotNull
    public static Table getTable(TileSet tileSet, FieldListener listener) {
        Table table = new Table(SKIN);
        table.defaults().growX().uniform();

        fillTable(tileSet, listener, table);

        return table;
    }

    private static void fillTable(TileSet tileSet, FieldListener listener, Table table) {
        table.add(IntegerEditor.create("ID", tileSet::setId, tileSet::getId, () -> {
        })).row();
        table.add(IntegerEditor.create("Rows", tileSet::setRows, tileSet::getRows, () -> {
            refresh(table, tileSet, listener);
            listener.onModify();
        })).row();
        table.add(IntegerEditor.create("Columns", tileSet::setCols, tileSet::getCols, () -> {
            refresh(table, tileSet, listener);
            listener.onModify();
        })).row();

        table.add(new Label("Tile Set images", SKIN)).row();
        for (int i = 0; i < tileSet.getRows(); i++) {
            int finalI = i;
            Table row = new Table();
            for (int j = 0; j < tileSet.getCols(); j++) {
                int finalJ = j;
                row.add(IntegerEditor.create("", id -> tileSet.setImage(finalI, finalJ, id), () -> tileSet.getImage(finalI, finalJ), listener));
            }
            table.add(row).row();
        }
    }

    private static void refresh(Table table, TileSet tileSet, FieldListener listener) {
        table.clear();
        fillTable(tileSet, listener, table);
    }

    private void addTable() {
        getContentTable().add(new ScrollPane(getTable(tileSet, () -> {
        }))).prefHeight(300).prefWidth(300);
    }

}
