package design.editors;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import design.editors.fields.FieldEditor.FieldListener;
import design.editors.fields.FieldProvider;
import design.editors.fields.IntegerEditor;
import model.descriptors.HeadDescriptor;
import org.jetbrains.annotations.NotNull;

import static launcher.DesignCenter.SKIN;

public class HeadEditor extends Dialog {

    private HeadDescriptor head;

    public HeadEditor(HeadDescriptor head) {
        super("Animation Editor", SKIN);
        this.head = head;
        addTable();
        button("Cancel", false);
        button("OK", head);
    }

    @NotNull
    public static Table getTable(HeadDescriptor head, FieldListener listener) {
        Table table = new Table(SKIN);
        table.defaults().growX().uniform();
        table.add(IntegerEditor.create("ID", id -> {
            head.setId(id);
            // TODO refactor: search all items that use this animation to change it
        }, head::getId, listener)).expandX().row();
        int[] heads = head.getIndexs();
        for (int i = 0; i < heads.length; i++) {
            final int j = i;
            table.add(IntegerEditor.create("Index: " + i, FieldProvider.IMAGE, index -> heads[j] = index, () -> heads[j], listener)).row();
        }

        return table;
    }

    private void addTable() {
        getContentTable().add(new ScrollPane(getTable(head, () -> {
        }))).prefHeight(300).prefWidth(300);
    }

}
