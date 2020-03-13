package design.editors.fields;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Timer;
import com.esotericsoftware.minlog.Log;
import design.designers.IDesigner;
import design.screens.ScreenManager;
import design.screens.views.View;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static launcher.DesignCenter.SKIN;

public class IntegerEditor extends FieldEditor<Integer> {

    private final Timer timer;

    private IntegerEditor(String label, FieldProvider fieldProvider, Consumer<Integer> consumer, Supplier<Integer> supplier) {
        super(label, fieldProvider, consumer, supplier);
        timer = new Timer();
    }

    public static Actor create(String label, Consumer<Integer> consumer, Supplier<Integer> supplier, FieldListener listener) {
        IntegerEditor integerEditor = new IntegerEditor(label, FieldProvider.NONE, consumer, supplier);
        integerEditor.addListener(listener);
        return integerEditor.getField();
    }

    public static Actor create(String label, FieldProvider fieldProvider, Consumer<Integer> consumer, Supplier<Integer> supplier, FieldListener listener) {
        IntegerEditor integerEditor = new IntegerEditor(label, fieldProvider, consumer, supplier);
        integerEditor.addListener(listener);
        return integerEditor.getField();
    }

    public static void showWarning(int t, TextField text, FieldProvider fieldProvider, Stage stage) {
        Dialog notNumber = new Dialog("Invalid Reference", SKIN);
        String type = fieldProvider.getScreen().getTitle();
        notNumber.text("This is not a valid reference of " + type);
        notNumber.button("OK");
        Vector2 coors = text.localToStageCoordinates(new Vector2(text.getX(), text.getY()));
        notNumber.setPosition(coors.x, coors.y);
        notNumber.show(stage, sequence(Actions.alpha(0), Actions.fadeIn(0.4f, Interpolation.fade)));
    }

    public static void showWarning(Screen current, TextField text) {
        if (current instanceof View && !text.getText().isEmpty()) {
            Stage stage = ((View) current).getStage();
            Dialog notNumber = new Dialog("Invalid format", SKIN);
            notNumber.text("Not valid integer, changes are not going to be set");
            notNumber.button("OK");
            Vector2 coors = text.localToStageCoordinates(new Vector2(text.getX(), text.getY()));
            notNumber.setPosition(coors.x, coors.y);
            notNumber.show(stage, sequence(Actions.alpha(0), Actions.fadeIn(0.4f, Interpolation.fade)));
        }
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
                onModify();
            }
        });
        return chooser;
    }

    @NotNull
    private TextField createIntegerField(Supplier<Integer> integer, Consumer<Integer> consumer) {
        TextField text = new TextField("" + integer.get(), SKIN);

        text.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                timer.clear();
                timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        Gdx.app.postRunnable(() -> {
                            Screen current = ScreenManager.getInstance().getCurrent();
                            if (current instanceof View) {
                                View view = (View) current;
                                try {
                                    int t = Integer.parseInt(text.getText());
                                    if (!getFieldProvider().equals(FieldProvider.NONE)) {
                                        if (!validInput(t, view.getDesigner())) {
                                            showWarning(t, text, getFieldProvider(), view.getStage());
                                            text.setText(getSupplier().get().toString());
                                            return;
                                        }
                                    }
                                    getConsumer().accept(t);
                                    onModify();
                                } catch (NumberFormatException ex) {
                                    Log.error(this.toString(), "Error creating Integer field", ex);
                                } finally {
                                    showWarning(current, text);
                                    text.setText(getSupplier().get().toString());
                                }
                            }
                        });
                    }

                    private boolean validInput(int ref, IDesigner designer) {
                        return designer.contains(ref);
                    }
                }, 0.5f);
            }
        });
        return text;
    }
}
