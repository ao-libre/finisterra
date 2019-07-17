package design.editors;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import design.screens.ScreenManager;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static launcher.DesignCenter.SKIN;

public class IntegerEditor extends FieldEditor<Integer> {

    private IntegerEditor(String label, FieldProvider fieldProvider, Consumer<Integer> consumer, Supplier<Integer> supplier) {
        super(label, fieldProvider, consumer, supplier);
    }

    public static Actor create(String label, Consumer<Integer> consumer, Supplier<Integer> supplier) {
        return new IntegerEditor(label, FieldProvider.NONE, consumer, supplier).getField();
    }

    public static Actor create(String label, FieldProvider fieldProvider, Consumer<Integer> consumer, Supplier<Integer> supplier) {
        return new IntegerEditor(label, fieldProvider, consumer, supplier).getField();
    }

    @Override
    protected Actor createSimpleEditor() {
        return createIntegerField(getSupplier(), getConsumer());
    }

    @Override
    protected Button createButton(TextField editor) {
        Button chooser = new Button(SKIN, "settings");
        chooser.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getFieldProvider().search(editor, getConsumer(), ScreenManager.getInstance().getCurrent());
            }
        });
        return chooser;
    }

    @NotNull
    public static TextField createIntegerField(Supplier<Integer> integer, Consumer<Integer> consumer) {
        TextField text = new TextField("" + integer.get(), SKIN);

        text.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    int t = Integer.parseInt(text.getText());
                    consumer.accept(t);
                } catch (NumberFormatException ignored) {

                }
            }
        });
        return text;
    }

}
