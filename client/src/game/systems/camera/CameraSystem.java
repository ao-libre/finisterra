package game.systems.camera;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import game.AOGame;
import shared.model.map.Tile;

import static com.artemis.E.E;

@Wire
public class CameraSystem extends BaseSystem {

    public static final float CAMERA_MIN_ZOOM = 1f;
    public static final float CAMERA_MAX_ZOOM = 1.3f;
    public static final float ZOOM_TIME = 0.5f;

    public OrthographicCamera camera;

    private final float minZoom;
    private final float maxZoom;

    private float desiredZoom;
    private float timeToCameraZoomTarget, cameraZoomOrigin, cameraZoomDuration;

    public CameraSystem() {
        this(CAMERA_MIN_ZOOM, CAMERA_MAX_ZOOM);
    }

    public CameraSystem(float minZoom, float maxZoom) {
        this(minZoom, maxZoom, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private CameraSystem(float minZoom, float maxZoom, float width, float height) {
        this.maxZoom = maxZoom;
        this.minZoom = minZoom;
        this.desiredZoom = minZoom;
        float zoomFactorInverter = 1f / minZoom;
        setupViewport(width * zoomFactorInverter,
                height * zoomFactorInverter);
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
        desiredZoom = MathUtils.clamp(desiredZoom, minZoom, maxZoom);
        timeToCameraZoomTarget = cameraZoomDuration = duration;
    }

    @Override
    protected void initialize() {
        super.initialize();
        E(world.create())
                .aOCamera()
                .worldPosOffsets();
    }
}
