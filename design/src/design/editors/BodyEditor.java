package design.editors;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import design.editors.fields.FieldEditor.FieldListener;
import design.editors.fields.IntegerEditor;
import model.descriptors.BodyDescriptor;
import model.descriptors.Descriptor;

public class BodyEditor extends DescriptorEditor {

    public BodyEditor(Descriptor descriptor) {
        super(descriptor);
    }

    public static Table create(BodyDescriptor descriptor, FieldListener listener) {
        return new BodyEditor(descriptor).getTable(descriptor, listener);
    }

    @Override
    protected void getExtraFields(Descriptor descriptor, Table table, FieldListener listener) {
        BodyDescriptor bodyDescriptor = (BodyDescriptor) descriptor;
        table.add(IntegerEditor.create("Head Offset X", bodyDescriptor::setHeadOffsetX, bodyDescriptor::getHeadOffsetX, listener)).row();
        table.add(IntegerEditor.create("Head Offset Y", bodyDescriptor::setHeadOffsetY, bodyDescriptor::getHeadOffsetY, listener)).row();
    }
}
