package design.screens.views;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import design.designers.DescriptorDesigner;
import design.editors.HeadEditor;
import graphics.StaticDescriptorActor;
import model.descriptors.Descriptor;
import model.descriptors.HeadDescriptor;
import org.jetbrains.annotations.NotNull;

public class HeadsView extends DescriptorView {
    public HeadsView() {
        super(new DescriptorDesigner(HeadDescriptor.class));
    }

    @NotNull
    @Override
    Table getTable(Descriptor descriptor) {
        return HeadEditor.getTable((HeadDescriptor) descriptor);
    }

    @Override
    protected DescriptorActor createPreviewActor() {
        return new StaticDescriptorActor(getAnimationHandler());
    }

}
