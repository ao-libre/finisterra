package ar.com.tamborindeguy.model.textures;

import ar.com.tamborindeguy.client.handlers.DescriptorHandler;
import ar.com.tamborindeguy.client.handlers.SurfaceHandler;
import ar.com.tamborindeguy.model.Graphic;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GameTexture {

	private TextureRegion textureRegion;

	public GameTexture(int grhIndex) {
		this(DescriptorHandler.getGraphic(grhIndex));
	}
	
	public GameTexture(Graphic graphic) {
		this.textureRegion = new TextureRegion(SurfaceHandler.get(String.valueOf(graphic.getFileNum())),
				graphic.getX(), graphic.getY(), graphic.getWidth(), graphic.getHeight());
		this.textureRegion.flip(false, true);
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
