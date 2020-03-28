package game.systems.camera;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import game.AOGame;
import game.systems.render.BatchRenderingSystem;
import shared.model.map.Tile;

@Wire
public class CameraSystem extends BaseSystem {

    public final static float ZOOM_TIME = 0.5f;
    private final float minZoom;
    private final float maxZoom;
    public OrthographicCamera camera;
    private float desiredZoom = AOGame.GAME_SCREEN_ZOOM;
    // member variables:
    private float timeToCameraZoomTarget, cameraZoomOrigin, cameraZoomDuration;


    private CameraSystem(float zoom, float maxZoom, float width, float height) {
        this.maxZoom = maxZoom;
        this.minZoom = zoom;
        float zoomFactorInverter = 1f / zoom;
        setupViewport(width * zoomFactorInverter,
                height * zoomFactorInverter);
    }

    public CameraSystem(float zoom) {
        this(zoom, AOGame.GAME_SCREEN_MAX_ZOOM);
    }

    public CameraSystem(float zoom, float maxZoom) {
        this(zoom, maxZoom, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private void setupViewport(float width, float height) {
        createGameCamera(width, height);
    }

    private void createGameCamera(float width, float height) {
        camera = new OrthographicCamera(Tile.TILE_PIXEL_WIDTH * 24, Tile.TILE_PIXEL_WIDTH * 24 * (height / width));
        camera.setToOrtho(true, Tile.TILE_PIXEL_WIDTH * 24, Tile.TILE_PIXEL_WIDTH * 24 * (height / width));
        camera.update();
    }

    @Override
    protected void processSystem() {
        if (timeToCameraZoomTarget >= 0) {
            timeToCameraZoomTarget -= getWorld().getDelta();
            float progress = timeToCameraZoomTarget < 0 ? 1 : 1f - timeToCameraZoomTarget / cameraZoomDuration;
            camera.zoom = Interpolation.fastSlow.apply(cameraZoomOrigin, desiredZoom, progress);
        }
    }

    public void zoom(int inout, float duration) {
        cameraZoomOrigin = camera.zoom;
        desiredZoom += inout * 0.025f;
        desiredZoom = MathUtils.clamp(desiredZoom, AOGame.GAME_SCREEN_ZOOM, maxZoom);
        timeToCameraZoomTarget = cameraZoomDuration = duration;
    }
}
