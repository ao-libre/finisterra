package design.screens.views;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import design.designers.DescriptorDesigner;
import design.editors.fields.FieldEditor;
import model.descriptors.Descriptor;
import org.jetbrains.annotations.NotNull;

public class AnimatedDecriptorView extends DescriptorView {

    public AnimatedDecriptorView(DescriptorDesigner designer) {
        super(designer);
    }

    @NotNull
    @Override
    Table getTable(Descriptor descriptor, FieldEditor.FieldListener listener) {
        return new Table();
    }

}
