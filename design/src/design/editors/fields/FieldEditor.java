package design.editors.fields;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static launcher.DesignCenter.SKIN;

public abstract class FieldEditor<T> implements IFieldEditor {

    private final FieldProvider fieldProvider;
    private final Consumer<T> consumer;
    private final Supplier<T> supplier;
    private final String label;
    private final Actor field;

    public FieldEditor(String label, FieldProvider fieldProvider, Consumer<T> consumer, Supplier<T> supplier) {
        this.label = label;
        this.fieldProvider = fieldProvider;
        this.consumer = consumer;
        this.supplier = supplier;
        this.field = createField();
    }

    public Consumer<T> getConsumer() {
        return consumer;
    }

    public FieldProvider getFieldProvider() {
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

    protected Actor createField() {
        Table field = new Table(SKIN);
        field.add(new Label(getLabel(), SKIN)).space(4);
        Actor editor = createSimpleEditor();
        field.add(editor).growX();
        if (shouldAddButton(editor)) {
            field.add(createButton((TextField) editor)).right();
        }
        return field;
    }

    private boolean shouldAddButton(Actor editor) {
        return !fieldProvider.equals(FieldProvider.NONE) && editor instanceof TextField;
    }

    protected Button createButton(TextField editor) {
        return new Button();
    }

    protected abstract Actor createSimpleEditor();
}
