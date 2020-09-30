package game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * A viewport that scales the world using {@link Scaling}, but limits the scaling to integer multiples or simple halving
 * if the zoom would be 0x. Handy for keeping a pixelated look on high-DPI screens. The {@code conversionX} and {@code conversionY}
 * configurations correspond to the scaling difference from a screen pixel to a world unit.
 * <p>
 * {@link Scaling#fit} keeps the aspect ratio by scaling the world up to fit the screen, adding black bars (letterboxing) for the
 * remaining space.
 * <p>
 * {@link Scaling#fill} keeps the aspect ratio by scaling the world up to take the whole screen (some of the world may be off
 * screen, potentially in both directions regardless of whether fill, fillX, or fillY is used).
 * <p>
 * {@link Scaling#stretch} (NOT RECOMMENDED) does not keep the aspect ratio, the world is scaled to take the whole screen. It is not pixel perfect.
 * <p>
 * {@link Scaling#none} keeps the aspect ratio by using a fixed size world (the world may not fill the screen or some of the world
 * may be off screen).
 *
 * @author Daniel Holderbaum
 * @author Nathan Sweet
 * @author Tommy Ettinger
 */
public class PixelPerfectViewport extends Viewport {

    private Scaling scaling;
    private int conversionX, conversionY;
    private float currentScale = 1;
    /**
     * Creates a new viewport using a new {@link OrthographicCamera}.
     */
    public PixelPerfectViewport(Scaling scaling, float worldWidth, float worldHeight, int conversionX, int conversionY) {
        this(scaling, worldWidth, worldHeight, conversionX, conversionY, new OrthographicCamera());
    }
    public PixelPerfectViewport(Scaling scaling, float worldWidth, float worldHeight, int conversion) {
        this(scaling, worldWidth, worldHeight, conversion, conversion, new OrthographicCamera());
    }

    public PixelPerfectViewport(Scaling scaling, float worldWidth, float worldHeight, int conversionX, int conversionY, Camera camera) {
        this.scaling = scaling;
        this.conversionX = conversionX;
        this.conversionY = conversionY;
        setWorldSize(worldWidth, worldHeight);
        setCamera(camera);
    }

    @Override
    public void update(int screenWidth, int screenHeight, boolean centerCamera) {
        //Vector2 scaled = scaling.apply(getWorldWidth(), getWorldHeight(), screenWidth, screenHeight);
        float worldWidth = getWorldWidth(), worldHeight = getWorldHeight();

        int viewportWidth = 0;
        int viewportHeight = 0;

        switch (scaling) {
            case fit: {
                float screenRatio = screenHeight / (float)screenWidth;
                float worldRatio = worldHeight / worldWidth;
                float scale = (int) (screenRatio > worldRatio ? screenWidth / (worldWidth * conversionX) : screenHeight / (worldHeight * conversionY));
                if (scale < 1) scale = 0.5f;
                viewportWidth = Math.round(worldWidth * scale);
                viewportHeight = Math.round(worldHeight * scale);
                this.currentScale = 1f / scale;
                break;
            }
            case fill: {
                float screenRatio = screenHeight / (float)screenWidth;
                float worldRatio = worldHeight / worldWidth;
                float scale = (int) Math.ceil(screenRatio < worldRatio ? screenWidth / (worldWidth * conversionX) : screenHeight / (worldHeight * conversionY));
                if (scale < 1) scale = 0.5f;
                viewportWidth = Math.round(worldWidth * scale);
                viewportHeight = Math.round(worldHeight * scale);
                this.currentScale = 1f / scale;
                break;
            }
            case fillX: {
                float scale = (int) Math.ceil(screenWidth / (worldWidth * conversionX));
                if (scale < 1) scale = 0.5f;
                viewportWidth = Math.round(worldWidth * scale);
                viewportHeight = Math.round(worldHeight * scale);
                this.currentScale = 1f / scale;
                break;
            }
            case fillY: {
                float scale = (int) Math.ceil(screenHeight / (worldHeight * conversionY));
                if (scale < 1) scale = 0.5f;
                viewportWidth = Math.round(worldWidth * scale);
                viewportHeight = Math.round(worldHeight * scale);
                this.currentScale = 1f / scale;
                break;
            }
            case stretch:
                viewportWidth = screenWidth;
                viewportHeight = screenHeight;
                break;
            case stretchX:
                viewportWidth = screenWidth;
                viewportHeight = (int) worldHeight;
                break;
            case stretchY:
                viewportWidth = (int) worldWidth;
                viewportHeight = screenHeight;
                break;
            case none:
            default:
                viewportWidth = (int) worldWidth;
                viewportHeight = (int) worldHeight;
                break;
        }
        // Center.
        setScreenBounds((screenWidth - viewportWidth * conversionX) / 2, (screenHeight - viewportHeight * conversionY) / 2, viewportWidth * conversionX, viewportHeight * conversionX);

        apply(centerCamera);
    }

    public Scaling getScaling() {
        return scaling;
    }
    
    public void setScaling(Scaling scaling) {
        this.scaling = scaling;
    }
    
    public float getCurrentScale() {
        return currentScale;
    }
}