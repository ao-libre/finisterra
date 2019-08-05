package design.editors;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import design.editors.fields.FieldEditor;
import design.editors.fields.FieldProvider;
import design.editors.fields.IntegerEditor;
import model.descriptors.Descriptor;
import org.jetbrains.annotations.NotNull;
import shared.interfaces.Constants;

import static launcher.DesignCenter.SKIN;

abstract class DescriptorEditor extends Dialog {

    private Descriptor descriptor;

    public DescriptorEditor(Descriptor descriptor) {
        super("Body Editor", SKIN);
        this.descriptor = descriptor;
        addTable();
        button("Cancel", false);
        button("OK", descriptor);
    }

    @NotNull
    public Table getTable(Descriptor descriptor, FieldEditor.FieldListener listener) {
        Table table = new Table(SKIN);
        table.defaults().growX().uniform();

        table.add(IntegerEditor.create("ID", descriptor::setId, descriptor::getId, listener)).row();
        getExtraFields(descriptor, table, listener);
        table.add(new Label("Animations: ", SKIN)).row();
        table.add(IntegerEditor.create("South", FieldProvider.ANIMATION, b -> descriptor.getIndexs()[Constants.Heading.SOUTH.toInt()] = b, () -> descriptor.getGraphic(Constants.Heading.SOUTH.toInt()), listener)).row();
        table.add(IntegerEditor.create("East", FieldProvider.ANIMATION, b -> descriptor.getIndexs()[Constants.Heading.EAST.toInt()] = b, () -> descriptor.getGraphic(Constants.Heading.EAST.toInt()), listener)).row();
        table.add(IntegerEditor.create("West", FieldProvider.ANIMATION, b -> descriptor.getIndexs()[Constants.Heading.WEST.toInt()] = b, () -> descriptor.getGraphic(Constants.Heading.WEST.toInt()), listener)).row();
        table.add(IntegerEditor.create("North", FieldProvider.ANIMATION, b -> descriptor.getIndexs()[Constants.Heading.NORTH.toInt()] = b, () -> descriptor.getGraphic(Constants.Heading.NORTH.toInt()), listener)).row();

        return table;
    }

    protected void getExtraFields(Descriptor descriptor, Table table, FieldEditor.FieldListener listener) {

    }

    private void addTable() {
        getContentTable().add(new ScrollPane(getTable(descriptor, () -> {
        }))).prefHeight(300).prefWidth(300);
    }

}
