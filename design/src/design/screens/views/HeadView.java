package design.screens.views;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import design.designers.HeadDesigner;
import design.designers.HeadDesigner.HeadParameters;
import design.editors.AnimationEditor;
import entity.character.parts.Head;
import entity.character.states.Heading;
import game.screens.WorldScreen;
import model.descriptors.HeadDescriptor;
import model.textures.AOAnimation;
import model.textures.AOTexture;
import model.textures.BundledAnimation;

import java.util.Comparator;

public class HeadView extends View<HeadDescriptor, HeadDesigner> implements WorldScreen {

    public HeadView() {
        super(new HeadDesigner(new HeadParameters()));
    }

    @Override
    Preview<HeadDescriptor> createPreview() {
        return new AnimationPreview();
    }

    @Override
    Preview<HeadDescriptor> createItemView() {
        AnimationItem animationItem = new AnimationItem();

        return animationItem;
    }

    @Override
    protected void sort(Array<HeadDescriptor> items) {
        items.sort(Comparator.comparingInt(HeadDescriptor::getId));
    }

    @Override
    protected void keyPressed(int keyCode) {

    }

    class AnimationItem extends Preview<HeadDescriptor> {

        private HeadDescriptor head;
        private Actor view;

        public AnimationItem() {
            super(SKIN);
        }

        @Override
        void show(HeadDescriptor head) {
            this.head = head;
            if (view != null) {
                removeActor(view);
            }
            //TODO view = AnimationEditor.getTable(head);
            add(view);
        }

        @Override
        HeadDescriptor get() {
            return head;
        }
    }

    class AnimationPreview extends Preview<HeadDescriptor> {

        private final Image image;
        private final Label label;
        private HeadDescriptor head;
        private AOTexture texture;

        public AnimationPreview() {
            super(SKIN);
            label = new Label("", SKIN);
            add(label).row();
            image = new Image();
            add(image);
        }

        @Override
        public void show(HeadDescriptor head) {
            this.head = head;
            label.setText(head.getId());
            texture = getAnimationHandler().getHeadAnimation(new Head(head.id), Heading.HEADING_SOUTH);
            setSize(texture.getTexture().getRegionWidth(), texture.getTexture().getRegionHeight());
        }

        @Override
        public HeadDescriptor get() {
            return head;
        }

        @Override
        public void act(float delta) {
            if (head != null) {
                TextureRegion graphic = texture.getTexture();
                if (graphic.isFlipY()) {
                    graphic.flip(false, true);
                }
                TextureRegionDrawable drawable = new TextureRegionDrawable(graphic);
                image.setDrawable(drawable);
            }
            super.act(delta);
        }
    }
}
