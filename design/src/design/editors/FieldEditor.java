package design.editors;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static design.screens.views.View.SKIN;

public abstract class FieldEditor<T> {

    private final FieldProvider<List<T>> fieldProvider;
    private final Consumer<T> consumer;
    private final Supplier<T> supplier;
    private final String label;
    private final Actor field;

    public FieldEditor(String label, FieldProvider<List<T>> fieldProvider, Consumer<T> consumer, Supplier<T> supplier) {
        this.label = label;
        this.fieldProvider = fieldProvider;
        this.consumer = consumer;
        this.supplier = supplier;
        this.field = createField();
    }

    public Consumer<T> getConsumer() {
        return consumer;
    }

    public FieldProvider<List<T>> getFieldProvider() {
        return fieldProvider;
    }

    public String getLabel() {
        return label;
    }

    public Actor getField() {
        return field;
    }

    public Supplier<T> getSupplier() {
        return supplier;
    }

    Actor createField() {
        Table field = new Table(SKIN);
        field.add(new Label(getLabel(), SKIN));
        List<T> items = getFieldProvider().get();
        if (items.size() > 1) {
            com.badlogic.gdx.scenes.scene2d.ui.List<T> list = new com.badlogic.gdx.scenes.scene2d.ui.List<>(SKIN);
            Array<T> itemsArray = new Array<>();
            items.forEach(itemsArray::add);
            list.setItems(itemsArray);
            list.setSelected(supplier.get());
            list.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    getConsumer().accept(list.getSelected());
                }
            });
            field.add(list);
        } else {
            field.add(createSimpleEditor());
        }
        return field;
    }

    protected abstract Actor createSimpleEditor();
}
