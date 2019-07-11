package design.editors;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static design.screens.views.View.SKIN;

public class IntegerEditor extends FieldEditor<Integer> {

    private IntegerEditor(String label, FieldProvider<List<Integer>> fieldProvider, Consumer<Integer> consumer, Supplier<Integer> supplier) {
        super(label, fieldProvider, consumer, supplier);
    }

    public static Actor simple(String label, Consumer<Integer> consumer, Supplier<Integer> supplier) {
        return new IntegerEditor(label, Collections::emptyList, consumer, supplier).getField();
    }

    public static Actor list(String label, FieldProvider<List<Integer>> fieldProvider, Consumer<Integer> consumer, Supplier<Integer> supplier) {
        return new IntegerEditor(label, fieldProvider, consumer, supplier).getField();
    }

    @Override
    protected Actor createSimpleEditor() {
        Integer integer = getSupplier().get();
        TextField text = new TextField("" + integer, SKIN);

        text.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    int t = Integer.parseInt(text.getText());
                    getConsumer().accept(t);
                } catch (NumberFormatException ignored) {

                }
            }
        });
        return text;
    }

}
