package design.screens.map.systems;

import camera.Focused;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import game.handlers.MapHandler;
import game.managers.MapManager;
import game.systems.render.world.RenderingSystem;
import position.WorldPos;
import shared.model.map.Map;
import shared.util.MapHelper;

@Wire(injectInherited = true)
public class MapDesignRenderingSystem extends RenderingSystem {

    private final MapHelper helper;
    private int current;
    private Map map;
    private MapManager mapManager;

    public MapDesignRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(Focused.class, WorldPos.class), batch, CameraKind.WORLD);
        helper = MapHandler.getHelper();
    }

    public Map loadMap(int i) {
        map = helper.getMap(i);
        current = i;
        return map;
    }

    @Override
    protected void begin() {
        getCamera().update();
        getBatch().setProjectionMatrix(getCamera().combined);
    }

    @Override
    protected void end() {

    }

    public int getCurrent() {
        return current;
    }

    @Override
    protected void process(E e) {
        if (map != null) {
            for (int i = 0; i < 4; i++) {
                getBatch().begin();
                mapManager.drawLayer(map, getBatch(), world.getDelta(), i);
                getBatch().end();
            }
        }
    }

    public void addMap(Map map) {
        this.map = map;
    }
}
