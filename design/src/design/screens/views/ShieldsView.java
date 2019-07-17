package design.screens.views;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import design.designers.DescriptorDesigner;
import model.descriptors.BodyDescriptor;
import model.descriptors.Descriptor;
import model.descriptors.ShieldDescriptor;
import org.jetbrains.annotations.NotNull;

public class ShieldsView extends DescriptorView {
    public ShieldsView() {
        super(new DescriptorDesigner(ShieldDescriptor.class));
    }

    @NotNull
    @Override
    Table getTable(Descriptor descriptor) {
        return null;
    }
}
