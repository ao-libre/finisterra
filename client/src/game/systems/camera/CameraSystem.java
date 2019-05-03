package game.systems.camera;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import shared.model.map.Tile;

/**
 * Setup and manages basic orthographic camera.
 *
 * @author Daan van Yperen
 */
public class CameraSystem extends BaseSystem {

    private final float zoom;
    public OrthographicCamera camera;
    public OrthographicCamera guiCamera;

    public CameraSystem(float zoom, float width, float height) {
        this.zoom = zoom;
        float zoomFactorInverter = 1f / zoom;
        setupViewport(width * zoomFactorInverter,
                height * zoomFactorInverter);
    }

    /**
     * @param zoom How much
     */
    public CameraSystem(float zoom) {
        this(zoom, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    protected void setupViewport(float width, float height) {
        createGameCamera(width, height);
        createGuiCamera(width, height);
    }

    private void createGuiCamera(float width, float height) {
        guiCamera = new OrthographicCamera(Tile.TILE_PIXEL_WIDTH * 15, Tile.TILE_PIXEL_WIDTH * 15 * (height / width));
        guiCamera.setToOrtho(false, Tile.TILE_PIXEL_WIDTH * 15, Tile.TILE_PIXEL_WIDTH * 15 * (height / width));
        guiCamera.update();
    }

    private void createGameCamera(float width, float height) {
        camera = new OrthographicCamera(Tile.TILE_PIXEL_WIDTH * 15, Tile.TILE_PIXEL_WIDTH * 15 * (height / width));
        camera.setToOrtho(true, Tile.TILE_PIXEL_WIDTH * 15, Tile.TILE_PIXEL_WIDTH * 15 * (height / width));
        camera.update();
    }

    @Override
    protected void processSystem() {

    }
}
