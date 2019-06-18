package game.systems.camera;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import game.AOGame;
import shared.model.map.Tile;

@Wire
public class CameraSystem extends BaseSystem {

    public final static float ZOOM_TIME = 0.5f;
    private final float zoom;
    public OrthographicCamera camera;
    public OrthographicCamera guiCamera;
    public float desiredZoom = AOGame.GAME_SCREEN_ZOOM;

    // member variables:
    float timeToCameraZoomTarget, cameraZoomOrigin, cameraZoomDuration;


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
        guiCamera = new OrthographicCamera(Tile.TILE_PIXEL_WIDTH * 24, Tile.TILE_PIXEL_WIDTH * 24 * (height / width));
        guiCamera.setToOrtho(false, Tile.TILE_PIXEL_WIDTH * 24, Tile.TILE_PIXEL_WIDTH * 24 * (height / width));
        guiCamera.update();
    }

    private void createGameCamera(float width, float height) {
        camera = new OrthographicCamera(Tile.TILE_PIXEL_WIDTH * 24, Tile.TILE_PIXEL_WIDTH * 24 * (height / width));
        camera.setToOrtho(true, Tile.TILE_PIXEL_WIDTH * 24, Tile.TILE_PIXEL_WIDTH * 24 * (height / width));
        camera.update();
    }

    @Override
    protected void processSystem() {
        // in render():
        if (timeToCameraZoomTarget >= 0) {
            timeToCameraZoomTarget -= getWorld().getDelta();
            float progress = timeToCameraZoomTarget < 0 ? 1 : 1f - timeToCameraZoomTarget / cameraZoomDuration;
            camera.zoom = Interpolation.fastSlow.apply(cameraZoomOrigin, desiredZoom, progress);
        }
    }

    public void zoom(int inout, float duration) {
        cameraZoomOrigin = camera.zoom;
        desiredZoom += inout * 0.025f;
        desiredZoom = MathUtils.clamp(desiredZoom, AOGame.GAME_SCREEN_ZOOM, AOGame.GAME_SCREEN_MAX_ZOOM);
        timeToCameraZoomTarget = cameraZoomDuration = duration;
    }
}
