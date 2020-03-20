package game.systems.render.world;

import camera.Focused;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.Entity;
import com.badlogic.gdx.graphics.g2d.Batch;
import entity.character.states.Buff;
import game.systems.OrderedEntityProcessingSystem;
import game.systems.camera.CameraSystem;

import java.util.Comparator;

public class BuffRenderingSystem extends OrderedEntityProcessingSystem {

    public static final float ALPHA = 0.5f;
    private static final int BORDER = 6;
    private final Batch batch;
    private CameraSystem cameraSystem;
    private int yOffset = 100;

    public BuffRenderingSystem(Batch batch) {
        super(Aspect.all(Focused.class, Buff.class));
        this.batch = batch;
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
        E player = E.E(e);

        player.buffBuffedAtributes().forEach((attrib, time) -> {

            player.buffBuffedAtributes().put(attrib, player.buffBuffedAtributes().get(attrib) - getWorld().getDelta());

            //drawCoordinates(50, yOffset, time - (world.getSystem(TimeSync.class).getRtt()/1000), attrib);

            this.yOffset += 50;
        });

        this.yOffset = 100;

    }

    @Override
    protected Comparator<? super Entity> getComparator() {
        return Comparator.comparingInt(entity -> E.E(entity).getWorldPos().y);
    }
}