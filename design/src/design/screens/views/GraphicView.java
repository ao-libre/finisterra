package design.screens.views;

import com.artemis.SuperMapper;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import design.designers.GraphicDesigner;
import design.systems.PreviewRenderingSystem;
import game.handlers.AnimationHandler;
import game.handlers.DescriptorHandler;
import game.handlers.ObjectHandler;
import game.screens.WorldScreen;
import game.systems.camera.CameraFocusSystem;
import game.systems.camera.CameraSystem;
import game.systems.render.world.CharacterRenderingSystem;
import model.textures.BundledAnimation;
import shared.model.Graphic;

import java.util.Comparator;

import static design.designers.GraphicDesigner.GraphicParameters;

public class GraphicView extends View<Graphic, GraphicDesigner> implements WorldScreen {

    public GraphicView() {
        super(new GraphicDesigner(new GraphicParameters()));
        createUI();
    }



    @Override
    Preview<Graphic> createPreview(Table viewTable) {
        GraphicPreview graphicPreview = new GraphicPreview();
        viewTable.add(graphicPreview.getPreviewContent()).right();
        return graphicPreview;
    }

    @Override
    protected void sort(Array<Graphic> items) {
        items.sort(Comparator.comparingInt(Graphic::getGrhIndex));
    }

    @Override
    protected void keyPressed(int keyCode) {

    }

    class GraphicPreview implements Preview<Graphic> {

        private SpriteBatch batch = new SpriteBatch();
        private int entityId = -1;
        private final Label label;
        private final Image image;
        Table previewContent = new Table(SKIN);
        private Graphic graphic;

        public GraphicPreview() {
            label = new Label("", SKIN);
            image = new Image();
            previewContent.add(label).row();
            previewContent.add(image);
        }

        public Table getPreviewContent() {
            return previewContent;
        }

        @Override
        public void show(Graphic graphic) {
            BundledAnimation graphicAnimation = getAnimationHandler().getGraphicAnimation(graphic.getGrhIndex());
            TextureRegion texture = graphicAnimation.getGraphic();
            TextureRegionDrawable drawable = new TextureRegionDrawable(texture);
            image.setDrawable(drawable);
        }

        @Override
        public Graphic get() {
            return graphic;
        }
    }
}
