package game.systems.map;

import com.artemis.Aspect;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import game.systems.camera.CameraSystem;
import map.Map;
import shared.map.AutoTiler;
import shared.map.model.MapDescriptor;

import static com.artemis.E.E;

@Wire
public class MapSystem extends IteratingSystem {

    private int mapId = -1;
    private OrthogonalTiledMapRenderer renderer;
    private CameraSystem cameraSystem;
    private ShapeRenderer shapeRenderer;

    public MapSystem() {
        super(Aspect.all(Map.class));
    }

    @Override
    protected void inserted(int mapId) {
        super.inserted(mapId);
        if (this.mapId != -1) {
            E(this.mapId).deleteFromWorld();
        }

        this.mapId = mapId;
        Map map = E(mapId).getMap();

        MapDescriptor descriptor = AutoTiler.load(map.width, map.height, Gdx.files.internal(map.path));
        TiledMap tiledMap = descriptor.create(map.tiles);
        if (renderer == null) {
            renderer = new OrthogonalTiledMapRenderer(tiledMap);
        } else {
            renderer.setMap(tiledMap);
        }

        shapeRenderer = new ShapeRenderer();
        
    }

    @Override
    protected void begin() {
        Gdx.gl.glClearColor(.5f, .7f, .9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        cameraSystem.camera.update();
    }

    @Override
    protected void process(int entityId) {
        if(renderer == null) {
            return;
        }
        renderer.setView(cameraSystem.camera);
        renderer.render();
    }
}
