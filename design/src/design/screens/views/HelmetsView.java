package design.screens.views;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import design.designers.DescriptorDesigner;
import design.editors.HelmetEditor;
import graphics.StaticDescriptorActor;
import model.descriptors.Descriptor;
import model.descriptors.HelmetDescriptor;
import org.jetbrains.annotations.NotNull;

public class HelmetsView extends DescriptorView {
    public HelmetsView() {
        super(new DescriptorDesigner(HelmetDescriptor.class));
    }

    @NotNull
    @Override
    Table getTable(Descriptor descriptor) {
        return HelmetEditor.create((HelmetDescriptor) descriptor);
    }

    @Override
    protected DescriptorActor createPreviewActor() {
        return new StaticDescriptorActor(getAnimationHandler());
    }

}
