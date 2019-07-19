package design.editors.fields;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static launcher.DesignCenter.SKIN;

public class BooleanEditor extends FieldEditor<Boolean> {

    private BooleanEditor(String label, FieldProvider fieldProvider, Consumer<Boolean> consumer, Supplier<Boolean> supplier) {
        super(label, fieldProvider, consumer, supplier);
    }

    public static Actor simple(String label, Consumer<Boolean> consumer, Supplier<Boolean> supplier) {
        return new BooleanEditor(label, FieldProvider.NONE, consumer, supplier).getField();
    }

    @Override
    protected Actor createField() {
        Table table = new Table();
        table.add(new CheckBox(getLabel(), SKIN)).left().growX();
        return table;
    }

    @Override
    protected Actor createSimpleEditor() {
        return null;
    }
}
