package design.screens.views;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import design.designers.DescriptorDesigner;
import design.editors.ShieldEditor;
import design.editors.fields.FieldEditor;
import model.descriptors.Descriptor;
import model.descriptors.ShieldDescriptor;
import org.jetbrains.annotations.NotNull;

public class ShieldsView extends DescriptorView {
    public ShieldsView() {
        super(new DescriptorDesigner(ShieldDescriptor.class));
    }

    @NotNull
    @Override
    Table getTable(Descriptor descriptor, FieldEditor.FieldListener listener) {
        return ShieldEditor.create((ShieldDescriptor) descriptor, listener);
    }
}
