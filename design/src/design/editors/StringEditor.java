package design.editors;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static design.screens.views.View.SKIN;

public class StringEditor extends FieldEditor<String> {

    private StringEditor(String label, FieldProvider<List<String>> fieldProvider, Consumer<String> consumer, Supplier<String> supplier) {
        super(label, fieldProvider, consumer, supplier);
    }

    @Override
    protected Actor createSimpleEditor() {
        TextField text = new TextField(getSupplier().get(), SKIN);

        text.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                getConsumer().accept(text.getText());
            }
        });

        return text;
    }

    public static Actor simple(String label, Consumer<String> consumer, Supplier<String> supplier) {
        StringEditor stringEditor = new StringEditor(label, Collections::emptyList, consumer, supplier);
        return stringEditor.getField();
    }

    public static Actor list(String label, FieldProvider<List<String>> fieldProvider, Consumer<String> consumer, Supplier<String> supplier) {
        StringEditor stringEditor = new StringEditor(label, fieldProvider, consumer, supplier);
        return stringEditor.getField();
    }


}
