package game.systems.map;

import com.artemis.Aspect;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import game.systems.camera.CameraSystem;
import map.Cave;
import shared.map.model.CaveToMap;

import java.util.Optional;

import static com.artemis.E.E;

@Wire
public class CaveSystem extends IteratingSystem {

    private int id = -1;
    private OrthogonalTiledMapRenderer renderer;
    private CameraSystem cameraSystem;

    public CaveSystem() {
        super(Aspect.all(Cave.class));
    }

    @Override
    protected void inserted(int id) {
        super.inserted(id);
        if (this.id != -1) {
            E(this.id).deleteFromWorld();
        }

        this.id = id;
        Cave cave = E(id).getCave();

        CaveToMap caveToMap = new CaveToMap(cave);
        TiledMap tiledMap = caveToMap.create();
        if (renderer == null) {
            renderer = new OrthogonalTiledMapRenderer(tiledMap);
        } else {
            renderer.setMap(tiledMap);
        }
    }

    public Optional<Cave> getCurrent() {
        return this.id != -1 && E(this.id).hasCave() ? Optional.ofNullable(E(this.id).getCave()) : Optional.empty();
    }

    public boolean isBlocked(int x, int y) {
        Optional<Cave> cave = getCurrent();
        return cave.isPresent() && cave.get().isBlocked(x, y);
    }

    @Override
    protected void begin() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        cameraSystem.camera.update();
    }

    @Override
    protected void process(int entityId) {
        if (renderer == null) {
            return;
        }
        renderer.setView(cameraSystem.camera);
        renderer.render();
    }
}
