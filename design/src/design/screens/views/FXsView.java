package design.screens.views;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import design.designers.DescriptorDesigner;
import design.editors.FXEditor;
import design.editors.fields.FieldEditor;
import model.descriptors.Descriptor;
import model.descriptors.FXDescriptor;
import org.jetbrains.annotations.NotNull;

public class FXsView extends DescriptorView {
    public FXsView() {
        super(new DescriptorDesigner(FXDescriptor.class));
    }

    @NotNull
    @Override
    Table getTable(Descriptor descriptor, FieldEditor.FieldListener listener) {
        return FXEditor.create((FXDescriptor) descriptor, listener);
    }
}
