package design.screens.views;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import design.designers.AnimationDesigner;
import design.designers.AnimationDesigner.AnimationParameters;
import design.dialogs.AnimationFromImages;
import design.editors.AnimationEditor;
import design.editors.fields.FieldEditor.FieldListener;
import game.screens.WorldScreen;
import model.textures.AOAnimation;
import model.textures.AOImage;
import model.textures.BundledAnimation;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static launcher.DesignCenter.SKIN;

public class AnimationView extends View<AOAnimation, AnimationDesigner> implements WorldScreen {

    public AnimationView() {
        super(new AnimationDesigner(new AnimationParameters()));
    }

    @Override
    Preview<AOAnimation> createPreview() {
        return new AnimationPreview();
    }

    @Override
    Editor<AOAnimation> createItemView() {
        return new AnimationItem();
    }

    @Override
    protected void sort(Array<AOAnimation> items) {
        items.sort(Comparator.comparingInt(AOAnimation::getId));
    }

    @Override
    protected void keyPressed(int keyCode) {

    }

    @Override
    public void refreshPreview() {
        AOAnimation animation = getItemView().get();
        getAnimationHandler().clearAnimation(animation);
        super.refreshPreview();
    }

    public void createAnimation(List<AOImage> images) {
        AnimationFromImages.show(images);
    }

    @Override
    protected void addButtons(Table buttons) {
        Button fromFile = new ImageButton(SKIN, "grid-dark");
        fromFile.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getDesigner().createFromFile();
            }
        });
        fromFile.addListener(new TextTooltip("Create animation from sprite sheet file", SKIN));
        buttons.add(fromFile).left();
        super.addButtons(buttons);
    }

    class AnimationItem extends Editor<AOAnimation> {

        AnimationItem() {
            super(SKIN);
        }

        @NotNull
        @Override
        protected Table getTable(FieldListener listener) {
            return AnimationEditor.getTable(current, listener);
        }

        @Override
        protected AOAnimation getCopy(AOAnimation to) {
            return new AOAnimation(to);
        }

    }

    class AnimationPreview extends Preview<AOAnimation> {

        private final Image image;
        private final Label label;
        private AOAnimation animation;
        private BundledAnimation bundledAnimation;

        AnimationPreview() {
            super(SKIN);
            label = new Label("", SKIN);
            add(label).row();
            image = new Image();
            add(image);
        }

        @Override
        public void show(AOAnimation animation) {
            this.animation = animation;
            label.setText(animation.getId());
            if (hasAnimation(animation)) {
                bundledAnimation = getAnimationHandler().getPreviewAnimation(animation);
                TextureRegion graphic = bundledAnimation.getGraphic();
                image.setSize(graphic.getRegionWidth(), graphic.getRegionHeight());
            }
        }

        boolean hasAnimation(AOAnimation animation) {
            return Stream.of(animation.getFrames()).flatMapToInt(Arrays::stream).anyMatch(i -> i > 0);
        }

        @Override
        public AOAnimation get() {
            return animation;
        }

        @Override
        public void act(float delta) {
            if (animation != null) {
                bundledAnimation.setAnimationTime(bundledAnimation.getAnimationTime() + delta);
                TextureRegion graphic = bundledAnimation.getGraphic();
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
