package model.textures;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AOTexture {

    private TextureRegion textureRegion;

    public AOTexture(AOImage image, Texture texture) {
        this(image,texture, true);
    }

    public AOTexture(AOImage image, Texture texture, boolean flipY) {
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
