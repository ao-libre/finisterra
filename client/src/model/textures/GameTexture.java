package model.textures;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import game.handlers.DescriptorHandler;
import game.handlers.SurfaceHandler;
import shared.model.Graphic;

public class GameTexture {

    private TextureRegion textureRegion;

    public GameTexture(int grhIndex) {
        this(grhIndex, true);
    }

    public GameTexture(int grhIndex, boolean flipY) {
        this(DescriptorHandler.getGraphic(grhIndex), flipY);
    }

    public GameTexture(Graphic graphic) {
        this(graphic, true);
    }

    public GameTexture(Graphic graphic, boolean flipY) {
        this.textureRegion = new TextureRegion(SurfaceHandler.get(String.valueOf(graphic.getFileNum())),
                graphic.getX(), graphic.getY(), graphic.getWidth(), graphic.getHeight());
        this.textureRegion.flip(false, flipY);
    }

    public void dispose() {
        this.textureRegion.getTexture().dispose();
    }

    public TextureRegion getGraphic() {
        return textureRegion;
    }

    public void setGraphic(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
    }

}
