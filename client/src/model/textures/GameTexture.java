package model.textures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import game.AOGame;
import game.handlers.DescriptorHandler;
import game.screens.GameScreen;
import shared.model.Graphic;

public class GameTexture {

    private TextureRegion textureRegion;

    public GameTexture(int grhIndex) {
        this(grhIndex, true);
    }

    public GameTexture(int grhIndex, boolean flipY) {
        this(GameScreen.getWorld().getSystem(DescriptorHandler.class).getGraphic(grhIndex), flipY);
    }

    public GameTexture(Graphic graphic) {
        this(graphic, true);
    }

    public GameTexture(Graphic graphic, boolean flipY) {
        AOGame game = (AOGame) Gdx.app.getApplicationListener();
        Texture texture = game.getAssetManager().getTexture(graphic.getFileNum());
        this.textureRegion = new TextureRegion(texture,
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
