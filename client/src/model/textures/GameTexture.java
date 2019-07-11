package model.textures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import game.AOGame;
import game.AssetManagerHolder;
import game.handlers.DescriptorHandler;
import game.screens.GameScreen;
import game.utils.WorldUtils;
import shared.model.Graphic;

public class GameTexture {

    private TextureRegion textureRegion;

    public GameTexture(int grhIndex) {
        this(grhIndex, true);
    }

    public GameTexture(int grhIndex, boolean flipY) {
        this(WorldUtils.getWorld().map(world -> world.getSystem(DescriptorHandler.class)).orElse(new DescriptorHandler()).getGraphic(grhIndex), flipY);
    }

    public GameTexture(Graphic graphic) {
        this(graphic, true);
    }

    public GameTexture(Graphic graphic, boolean flipY) {
        AssetManagerHolder game = (AssetManagerHolder) Gdx.app.getApplicationListener();
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
