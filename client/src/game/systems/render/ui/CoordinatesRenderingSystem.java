package game.systems.render.ui;

import camera.Focused;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import game.systems.OrderedEntityProcessingSystem;
import game.systems.camera.CameraSystem;
import game.utils.Skins;
import position.WorldPos;

import java.util.Comparator;

import static com.artemis.E.E;

@Wire
public class CoordinatesRenderingSystem extends OrderedEntityProcessingSystem {

    public static final float ALPHA = 0.5f;
    private static final int BORDER = 6;
    private SpriteBatch batch;
    private CameraSystem cameraSystem;
    private final Label coordLabel;

    public CoordinatesRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(Focused.class, WorldPos.class));
        this.batch = batch;
        coordLabel = new Label("", Skins.COMODORE_SKIN);
    }

    @Override
    protected void begin() {
        cameraSystem.guiCamera.update();
        batch.setProjectionMatrix(cameraSystem.guiCamera.combined);
        batch.begin();
    }

    @Override
    protected void end() {
        batch.end();
    }

    @Override
    protected void process(Entity e) {
        E player = E(e);
        WorldPos worldPos = player.getWorldPos();

        drawCoordinates(50, 50, worldPos);
    }

    private void drawCoordinates(int offsetX, int offsetY, WorldPos worldPos) {
        String worldPosString = "[" + worldPos.map + "-" + worldPos.x + "-" + worldPos.y + "]";
        coordLabel.setText(worldPosString);
        float fontX = cameraSystem.guiCamera.viewportWidth - coordLabel.getPrefWidth() - offsetX;
        float fontY = cameraSystem.guiCamera.viewportHeight - offsetY;
        coordLabel.setPosition(fontX, fontY);
        coordLabel.draw(batch, 1);
    }

    @Override
    protected Comparator<? super Entity> getComparator() {
        return Comparator.comparingInt(entity -> E(entity).getWorldPos().y);
    }
}
