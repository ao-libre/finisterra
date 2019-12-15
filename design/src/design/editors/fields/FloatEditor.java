package design.editors.fields;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Timer;
import com.esotericsoftware.minlog.Log;
import design.screens.ScreenManager;
import design.screens.views.View;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static launcher.DesignCenter.SKIN;

public class FloatEditor extends FieldEditor<Float> {

    private FloatEditor(String label, FieldProvider fieldProvider, Consumer<Float> consumer, Supplier<Float> supplier) {
        super(label, fieldProvider, consumer, supplier);
    }

    public static Actor simple(String label, Consumer<Float> consumer, Supplier<Float> supplier, FieldListener listener) {
        FloatEditor floatEditor = new FloatEditor(label, FieldProvider.NONE, consumer, supplier);
        floatEditor.addListener(listener);
        return floatEditor.getField();
    }

    @Override
    protected Actor createSimpleEditor() {
        Float f = getSupplier().get();
        TextField text = new TextField("" + f, SKIN);

        text.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Timer.instance().clear();
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        Gdx.app.postRunnable(() -> {
                            Screen current = ScreenManager.getInstance().getCurrent();
                            if (current instanceof View) {
                                try {
                                    float t = Float.parseFloat(text.getText());
                                    getConsumer().accept(t);
                                    onModify();
                                } catch (NumberFormatException ex) {
                                    Log.error(this.toString(), "Error creating simple editor.", ex);
                                } finally {
                                    IntegerEditor.showWarning(current, text);
                                    text.setText(getSupplier().get().toString());
                                }
                            }
                        });
                    }
                }, 0.5f);
            }
        });

        return text;
    }

}
