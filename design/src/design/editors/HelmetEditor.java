package design.editors;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import design.editors.fields.FieldEditor.FieldListener;
import design.editors.fields.FieldProvider;
import design.editors.fields.IntegerEditor;
import model.descriptors.Descriptor;
import model.descriptors.HelmetDescriptor;
import org.jetbrains.annotations.NotNull;
import shared.interfaces.Constants;

import static launcher.DesignCenter.SKIN;

public class HelmetEditor extends DescriptorEditor {

    public HelmetEditor(Descriptor descriptor) {
        super(descriptor);
    }

    public static Table create(HelmetDescriptor descriptor, FieldListener listener) {
        return new HelmetEditor(descriptor).getTable(descriptor, listener);
    }

    @NotNull
    @Override
    public Table getTable(Descriptor descriptor, FieldListener listener) {
        Table table = new Table(SKIN);
        table.defaults().growX().uniform();

        table.add(IntegerEditor.create("ID", descriptor::setId, descriptor::getId, listener)).row();

        getExtraFields(descriptor, table, listener);

        table.add(new Label("Animations: ", SKIN)).row();
        table.add(IntegerEditor.create("South", FieldProvider.IMAGE, b -> descriptor.getIndexs()[Constants.Heading.SOUTH.toInt()] = b, () -> descriptor.getGraphic(Constants.Heading.SOUTH.toInt()), listener)).row();
        table.add(IntegerEditor.create("East", FieldProvider.IMAGE, b -> descriptor.getIndexs()[Constants.Heading.EAST.toInt()] = b, () -> descriptor.getGraphic(Constants.Heading.EAST.toInt()), listener)).row();
        table.add(IntegerEditor.create("West", FieldProvider.IMAGE, b -> descriptor.getIndexs()[Constants.Heading.WEST.toInt()] = b, () -> descriptor.getGraphic(Constants.Heading.WEST.toInt()), listener)).row();
        table.add(IntegerEditor.create("North", FieldProvider.IMAGE, b -> descriptor.getIndexs()[Constants.Heading.NORTH.toInt()] = b, () -> descriptor.getGraphic(Constants.Heading.NORTH.toInt()), listener)).row();

        return table;
    }
}
