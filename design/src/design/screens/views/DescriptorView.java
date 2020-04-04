package design.screens.views;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.minlog.Log;
import design.designers.DescriptorDesigner;
import design.editors.fields.FieldEditor.FieldListener;
import game.screens.WorldScreen;
import design.graphic.AOAnimationActor;
import model.descriptors.BodyDescriptor;
import model.descriptors.Descriptor;
import model.descriptors.FXDescriptor;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;

import static launcher.DesignCenter.SKIN;

public abstract class DescriptorView<T extends Descriptor> extends View<T, DescriptorDesigner<T>> implements WorldScreen {

    public DescriptorView(DescriptorDesigner<T> designer) {
        super(designer);
    }

    public static Descriptor copy(Descriptor descriptor, Class<? extends Descriptor> clazz) {
        Descriptor newDescriptor = null;
        try {
            Constructor<? extends Descriptor> constructor = clazz.getConstructor();
            newDescriptor = constructor.newInstance();
            for (int i = 0; i < (clazz.equals(FXDescriptor.class) ? 1 : 4); i++) {
                newDescriptor.getIndexs()[i] = descriptor.getGraphic(i);
            }
            newDescriptor.setId(descriptor.getId());
            if (clazz.equals(BodyDescriptor.class)) {
                BodyDescriptor copy = (BodyDescriptor) newDescriptor;
                BodyDescriptor original = (BodyDescriptor) descriptor;
                copy.setHeadOffsetX(original.getHeadOffsetX());
                copy.setHeadOffsetY(original.getHeadOffsetY());
            } else if (clazz.equals(FXDescriptor.class)) {
                FXDescriptor copy = (FXDescriptor) newDescriptor;
                FXDescriptor original = (FXDescriptor) descriptor;
                copy.setOffsetX(original.getOffsetX());
                copy.setOffsetY(original.getOffsetY());
            }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            Log.error("DescriptorView", "Error at copy()", e);
        }
        return newDescriptor;
    }

    @Override
    protected void sort(Array<T> items) {
        items.sort(Comparator.comparingInt(Descriptor::getId));
    }

    @Override
    Editor<T> createItemView() {
        return new DescriptorItem();
    }

    @Override
    Preview<T> createPreview() {
        return new DescriptorPreview();
    }

    @Override
    protected void keyPressed(int keyCode) {
    }

    @NotNull
    abstract Table getTable(Descriptor descriptor, FieldListener listener);

    protected DescriptorActor createPreviewActor() {
        return new AOAnimationActor(getAnimationHandler());
    }

    protected void setContent(DescriptorActor previewActor, Descriptor descriptor) {
        previewActor.setDescriptor(descriptor);
    }

    class DescriptorItem extends Editor<T> {

        public DescriptorItem() {
            super(SKIN);
        }

        @NotNull
        @Override
        protected Table getTable(FieldListener listener) {
            return DescriptorView.this.getTable(current, listener);
        }

        @Override
        protected T getCopy(T to) {
            return (T) copy(to, getDesigner().gettClass());
        }

    }

    class DescriptorPreview extends Preview<T> {

        private DescriptorActor previewActor;
        private Label label;
        private Descriptor descriptor;

        public DescriptorPreview() {
            super(SKIN);
        }

        void init() {
            Button table = new Button(SKIN, "color-base-static");
            table.defaults().space(5);
            Table buttons = new Table();
            label = new Label("", SKIN);
            Container anim = new Container();
            previewActor = createPreviewActor();
            Button move = new Button(SKIN, "switch");
            move.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    previewActor.move();
                }
            });
            buttons.add(move).right();
            Button rotate = new Button(SKIN, "colorwheel");
            rotate.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    previewActor.rotate();
                }
            });
            buttons.add(rotate).right();
            table.add(buttons).right();
            table.row();
            anim.setActor(previewActor);
            table.add(anim).row();
            table.add(label);
            add(table);
        }

        @Override
        public void show(Descriptor descriptor) {
            if (this.descriptor == null) {
                init();
            }
            this.descriptor = descriptor;
            label.setText(descriptor.getId());
            setContent(previewActor, descriptor);
        }

        @Override
        public T get() {
            return (T) descriptor;
        }

    }


}
