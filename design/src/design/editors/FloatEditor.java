package design.editors;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import design.screens.ScreenManager;
import design.screens.views.View;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static launcher.DesignCenter.SKIN;

public class FloatEditor extends FieldEditor<Float> {

    private FloatEditor(String label, FieldProvider fieldProvider, Consumer<Float> consumer, Supplier<Float> supplier) {
        super(label, fieldProvider, consumer, supplier);
    }

    public static Actor simple(String label, Consumer<Float> consumer, Supplier<Float> supplier) {
        return new FloatEditor(label, FieldProvider.NONE, consumer, supplier).getField();
    }

    public static Actor list(String label, FieldProvider fieldProvider, Consumer<Float> consumer, Supplier<Float> supplier) {
        return new FloatEditor(label, fieldProvider, consumer, supplier).getField();
    }

    @Override
    protected Actor createSimpleEditor() {
        Float f = getSupplier().get();
        TextField text = new TextField("" + f, SKIN);

        text.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    float t = Float.parseFloat(text.getText());
                    getConsumer().accept(t);
                    Screen current = ScreenManager.getInstance().getCurrent();
                    if (current instanceof View) {
                        ((View) current).refreshPreview();
                    }
                } catch (NumberFormatException ignored) {

                }
            }
        });
        return text;
    }

}
