package design.screens.views;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import design.designers.DescriptorDesigner;
import design.editors.BodyEditor;
import design.editors.fields.FieldEditor;
import model.descriptors.BodyDescriptor;
import model.descriptors.Descriptor;
import org.jetbrains.annotations.NotNull;

public class BodiesView extends DescriptorView {
    public BodiesView() {
        super(new DescriptorDesigner(BodyDescriptor.class));
    }

    @NotNull
    @Override
    Table getTable(Descriptor descriptor, FieldEditor.FieldListener listener) {
        return BodyEditor.create((BodyDescriptor) descriptor, listener);
    }
}
