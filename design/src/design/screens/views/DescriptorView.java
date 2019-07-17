package design.screens.views;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import design.designers.DescriptorDesigner;
import game.screens.WorldScreen;
import graphics.AOAnimationActor;
import model.descriptors.Descriptor;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

import static launcher.DesignCenter.SKIN;

public abstract class DescriptorView<T extends Descriptor> extends View<T, DescriptorDesigner<T>> implements WorldScreen {

    public DescriptorView(DescriptorDesigner<T> designer) {
        super(designer);
    }

    @Override
    protected void sort(Array<T> items) {
        items.sort(Comparator.comparingInt(Descriptor::getId));
    }

    @Override
    Preview<T> createItemView() {
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
    abstract Table getTable(Descriptor descriptor);

    protected DescriptorActor createPreviewActor() {
        return new AOAnimationActor(getAnimationHandler());
    }

    protected void setContent(DescriptorActor previewActor, Descriptor descriptor) {
        previewActor.setDescriptor(descriptor);
    }

    class DescriptorItem extends Preview<T> {

        private Descriptor descriptor;
        private Actor view;

        public DescriptorItem() {
            super(SKIN);
        }

        @Override
        void show(Descriptor descriptor) {
            this.descriptor = descriptor;
            if (view != null) {
                removeActor(view);
            }
            view = getTable(descriptor);
            add(view);
        }

        @Override
        T get() {
            return (T) descriptor;
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
