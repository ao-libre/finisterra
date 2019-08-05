package design.editors;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import design.editors.fields.FieldEditor.FieldListener;
import model.descriptors.Descriptor;
import model.descriptors.ShieldDescriptor;

public class ShieldEditor extends DescriptorEditor {

    public ShieldEditor(Descriptor descriptor) {
        super(descriptor);
    }

    public static Table create(ShieldDescriptor descriptor, FieldListener listener) {
        return new ShieldEditor(descriptor).getTable(descriptor, listener);
    }
}
