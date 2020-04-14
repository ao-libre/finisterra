package design.screens.views;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import design.designers.DescriptorDesigner;
import design.editors.HelmetEditor;
import design.editors.fields.FieldEditor;
import design.graphic.StaticDescriptorActor;
import model.descriptors.Descriptor;
import model.descriptors.HelmetDescriptor;
import org.jetbrains.annotations.NotNull;

public class HelmetsView extends DescriptorView {
    public HelmetsView() {
        super(new DescriptorDesigner(HelmetDescriptor.class));
    }

    @NotNull
    @Override
    Table getTable(Descriptor descriptor, FieldEditor.FieldListener listener) {
        return HelmetEditor.create((HelmetDescriptor) descriptor, listener);
    }

    @Override
    protected DescriptorActor createPreviewActor() {
        return new StaticDescriptorActor(getAnimationHandler());
    }

}
