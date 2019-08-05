package design.editors;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import design.editors.fields.FieldEditor;
import design.editors.fields.FieldProvider;
import design.editors.fields.FloatEditor;
import design.editors.fields.IntegerEditor;
import design.screens.ScreenManager;
import design.screens.views.View;
import model.textures.AOAnimation;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static launcher.DesignCenter.SKIN;

public class AnimationEditor extends Dialog {

    private AOAnimation animation;

    public AnimationEditor(AOAnimation animation) {
        super("Animation Editor", SKIN);
        this.animation = animation;
        addTable();
        button("Cancel", false);
        button("OK", animation);
    }

    @NotNull
    public static Table getTable(AOAnimation animation, FieldEditor.FieldListener listener) {
        Table table = new Table(SKIN);
        table.defaults().growX().uniform();
        createContent(animation, listener, table);
        return table;
    }

    public static void createContent(AOAnimation animation, FieldEditor.FieldListener listener, Table table) {
        table.add(IntegerEditor.create("ID", id -> {
            animation.setId(id);
            // TODO refactor: search all items that use this animation to change it
        }, animation::getId, listener)).expandX().row();
        table.add(FloatEditor.simple("Speed", animation::setSpeed, animation::getSpeed, listener)).row();
        int[] frames = animation.getFrames();
        for (int i = 0; i < frames.length; i++) {
            Table frameTable = new Table();
            int finalI = i;
            Actor actor = IntegerEditor.create("Frame-" + i, FieldProvider.IMAGE, integer -> frames[finalI] = integer, () -> frames[finalI], listener);
            Button removeFrame = new Button(SKIN, "delete");
            removeFrame.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    table.clear();
                    int[] newFrames = new int[frames.length - 1];
                    for (int j = 0; j < frames.length; j++) {
                        if (j != finalI) {
                            int index = j < finalI ? j : j - 1;
                            newFrames[index] = frames[j];
                        }
                    }
                    animation.setFrames(newFrames);
                    createContent(animation, listener, table);
                    Screen current = ScreenManager.getInstance().getCurrent();
                    ((View) current).getItemView().setState(View.State.MODIFIED);
                }
            });
            frameTable.add(actor).growX();
            frameTable.add(removeFrame);
            table.add(frameTable).row();
        }
        Button addFrame = new TextButton("Frame", SKIN, "new");
        addFrame.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                table.clear();
                animation.setFrames(Arrays.copyOf(frames, frames.length + 1));
                animation.getFrames()[animation.getFrames().length - 1] = 0;
                createContent(animation, listener, table);
                Screen current = ScreenManager.getInstance().getCurrent();
                ((View) current).getItemView().setState(View.State.MODIFIED);
            }
        });
        table.add(addFrame).fill(false, false).left();
    }

    private void addTable() {
        getContentTable().add(new ScrollPane(getTable(animation, () -> {
        }))).prefHeight(300).prefWidth(300);
    }

}
