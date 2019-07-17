package design.screens.views;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import design.designers.DescriptorDesigner;
import model.descriptors.Descriptor;
import model.descriptors.FXDescriptor;
import model.descriptors.ShieldDescriptor;
import org.jetbrains.annotations.NotNull;

public class FXsView extends DescriptorView {
    public FXsView() {
        super(new DescriptorDesigner(FXDescriptor.class));
    }

    @NotNull
    @Override
    Table getTable(Descriptor descriptor) {
        return new Table();
    }
}
