package design.screens.map.systems;

import component.camera.Focused;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import game.systems.resources.MapSystem;
import game.systems.map.MapManager;
import game.systems.render.BatchRenderingSystem;
import game.systems.render.world.RenderingSystem;
import game.utils.Colors;
import component.position.WorldPos;
import shared.model.map.Map;
import shared.model.map.Tile;
import shared.util.MapHelper;

@Wire(injectInherited = true)
public class MapDesignRenderingSystem extends RenderingSystem {

    private final MapHelper helper;
    ShapeRenderer sr = new ShapeRenderer();
    private int current;
    private Map map;
    private MapManager mapManager;
    private boolean showExit;
    private boolean showBlocks;
    private boolean showGrid;
    private BatchRenderingSystem batchRenderingSystem;

    public MapDesignRenderingSystem() {
        super(Aspect.all(Focused.class, WorldPos.class));
        helper = MapSystem.getHelper();
        sr.setColor(Colors.TRANSPARENT_RED);
        sr.setAutoShapeType(true);
    }

    public Map loadMap(int i) {
        map = helper.getMap(i);
        current = i;
        return map;
    }

    @Override
    protected void begin() {
        getCamera().update();
        batchRenderingSystem.getBatch().setProjectionMatrix(getCamera().combined);
        sr.setProjectionMatrix(getCamera().combined);
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
                batchRenderingSystem.getBatch().begin();
                mapManager.drawLayer(map, world.getDelta(), i, showExit, showBlocks);
                batchRenderingSystem.getBatch().end();
            }
            if (showGrid) {
                sr.begin();
                float start = Tile.TILE_PIXEL_HEIGHT;
                for (int i = 0; i < map.getHeight(); i++) {
                    // draw col
                    float x = (i + 1) * Tile.TILE_PIXEL_WIDTH;
                    sr.line(x, start, x, map.getHeight() * Tile.TILE_PIXEL_HEIGHT);
                    // draw row
                    sr.line(start, x, map.getWidth() * Tile.TILE_PIXEL_WIDTH, x);
                }
                sr.end();
            }
        }
        // draw tiles lines
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public void toggleBlocks() {
        showBlocks = !showBlocks;
    }

    public void toggleExits() {
        showExit = !showExit;
    }

    public void toggleGrid() {
        showGrid = !showGrid;
    }
}
