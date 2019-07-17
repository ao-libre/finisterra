package design.editors;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
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

    private void addTable() {
        getContentTable().add(new ScrollPane(getTable(head))).prefHeight(300).prefWidth(300);
    }

    @NotNull
    public static Table getTable(HeadDescriptor head) {
        Table table = new Table(SKIN);
        table.add(IntegerEditor.create("ID", id -> {
            head.setId(id);
            // TODO refactor: search all items that use this animation to change it
        }, head::getId)).expandX().row();
        int[] heads = head.getIndexs();
        for (int i = 0; i < heads.length; i++) {
            final int j = i;
            table.add(IntegerEditor.create("Index: " + i, FieldProvider.IMAGE, index -> heads[j] = index, () -> heads[j])).row();
        }

        return table;
    }

}
