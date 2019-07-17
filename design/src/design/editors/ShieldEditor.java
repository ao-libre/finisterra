package design.editors;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import model.descriptors.Descriptor;
import model.descriptors.ShieldDescriptor;

public class ShieldEditor extends DescriptorEditor {

    public ShieldEditor(Descriptor descriptor) {
        super(descriptor);
    }

    public static Table create(ShieldDescriptor descriptor) {
        return new ShieldEditor(descriptor).getTable(descriptor);
    }
}
