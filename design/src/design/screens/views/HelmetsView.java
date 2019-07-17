package design.screens.views;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import design.designers.DescriptorDesigner;
import design.editors.HeadEditor;
import graphics.StaticDescriptorActor;
import model.descriptors.Descriptor;
import model.descriptors.HeadDescriptor;
import model.descriptors.HelmetDescriptor;
import model.textures.AOTexture;
import org.jetbrains.annotations.NotNull;

public class HelmetsView extends DescriptorView {
    public HelmetsView() {
        super(new DescriptorDesigner(HelmetDescriptor.class));
    }

    @NotNull
    @Override
    Table getTable(Descriptor descriptor) {
        return new Table();
    }

    @Override
    protected DescriptorActor createPreviewActor() {
        return new StaticDescriptorActor(getAnimationHandler());
    }

}
