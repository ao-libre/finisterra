package design.editors;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import design.editors.fields.IntegerEditor;
import model.descriptors.BodyDescriptor;
import model.descriptors.Descriptor;

public class BodyEditor extends DescriptorEditor {

    public BodyEditor(Descriptor descriptor) {
        super(descriptor);
    }

    public static Table create(BodyDescriptor descriptor) {
        return new BodyEditor(descriptor).getTable(descriptor);
    }

    @Override
    protected void getExtraFields(Descriptor descriptor, Table table) {
        BodyDescriptor bodyDescriptor = (BodyDescriptor) descriptor;
        table.add(IntegerEditor.create("Head Offset X", bodyDescriptor::setHeadOffsetX, bodyDescriptor::getHeadOffsetX)).row();
        table.add(IntegerEditor.create("Head Offset Y", bodyDescriptor::setHeadOffsetY, bodyDescriptor::getHeadOffsetY)).row();
    }
}
