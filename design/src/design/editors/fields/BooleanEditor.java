package design.editors.fields;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static launcher.DesignCenter.SKIN;

public class BooleanEditor extends FieldEditor<Boolean> {

    private BooleanEditor(String label, FieldProvider fieldProvider, Consumer<Boolean> consumer, Supplier<Boolean> supplier) {
        super(label, fieldProvider, consumer, supplier);
    }

    public static Actor simple(String label, Consumer<Boolean> consumer, Supplier<Boolean> supplier, FieldListener listener) {
        BooleanEditor booleanEditor = new BooleanEditor(label, FieldProvider.NONE, consumer, supplier);
        booleanEditor.addListener(listener);
        return booleanEditor.getField();
    }

    @Override
    protected Actor createField() {
        ImageTextButton bool = new ImageTextButton(getLabel(), SKIN, "switch");
        bool.setChecked(getSupplier().get());
        bool.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                getConsumer().accept(bool.isChecked());
            }
        });
        return bool;
    }

    @Override
    protected Actor createSimpleEditor() {
        return null;
    }
}
