package model.textures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import game.AssetManagerHolder;
import game.handlers.DescriptorHandler;
import game.utils.WorldUtils;
import shared.model.Graphic;

public class AOTexture {

    private TextureRegion textureRegion;

    public AOTexture(AOImage image) {
        this(image, true);
    }

    public AOTexture(AOImage image, boolean flipY) {
        AssetManagerHolder game = (AssetManagerHolder) Gdx.app.getApplicationListener();
        Texture texture = game.getAssetManager().getTexture(image.getFileNum());
        this.textureRegion = new TextureRegion(texture,
                image.getX(), image.getY(), image.getWidth(), image.getHeight());
        this.textureRegion.flip(false, flipY);
    }

    public void dispose() {
        this.textureRegion.getTexture().dispose();
    }

    public TextureRegion getTexture() {
        return textureRegion;
    }

    public void setTexture(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
    }

}