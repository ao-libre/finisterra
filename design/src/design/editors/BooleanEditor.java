package design.editors;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static design.screens.views.View.SKIN;

public class BooleanEditor extends FieldEditor<Boolean> {

    private BooleanEditor(String label, FieldProvider<List<Boolean>> fieldProvider, Consumer<Boolean> consumer, Supplier<Boolean> supplier) {
        super(label, fieldProvider, consumer, supplier);
    }

    public static Actor simple(String label, Consumer<Boolean> consumer, Supplier<Boolean> supplier) {
        return new BooleanEditor(label, Collections::emptyList, consumer, supplier).getField();
    }

    @Override
    Actor createField() {
        return createSimpleEditor();
    }

    @Override
    protected Actor createSimpleEditor() {
        Table table = new Table();
        table.add(new CheckBox(getLabel(), SKIN));
        return table;
    }
}