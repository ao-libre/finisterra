package design.editors;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import design.editors.fields.FieldEditor.FieldListener;
import design.editors.fields.FieldProvider;
import design.editors.fields.IntegerEditor;
import model.descriptors.Descriptor;
import model.descriptors.FXDescriptor;
import org.jetbrains.annotations.NotNull;

import static launcher.DesignCenter.SKIN;

public class FXEditor extends DescriptorEditor {

    public FXEditor(Descriptor descriptor) {
        super(descriptor);
    }

    public static Table create(FXDescriptor descriptor, FieldListener listener) {
        return new FXEditor(descriptor).getTable(descriptor, listener);
    }

    @NotNull
    @Override
    public Table getTable(Descriptor descriptor, FieldListener listener) {
        Table table = new Table(SKIN);
        table.defaults().growX().uniform();

        table.add(IntegerEditor.create("ID", descriptor::setId, descriptor::getId, listener)).row();

        getExtraFields(descriptor, table, listener);

        table.add(IntegerEditor.create("Animation", FieldProvider.ANIMATION, b -> descriptor.getIndexs()[0] = b, () -> descriptor.getGraphic(0), listener)).row();

        return table;
    }

    @Override
    protected void getExtraFields(Descriptor descriptor, Table table, FieldListener listener) {
        FXDescriptor fxDescriptor = (FXDescriptor) descriptor;
        table.add(IntegerEditor.create("Offset X", fxDescriptor::setOffsetX, fxDescriptor::getOffsetX, listener)).row();
        table.add(IntegerEditor.create("Offset Y", fxDescriptor::setOffsetY, fxDescriptor::getOffsetY, listener)).row();
    }
}
