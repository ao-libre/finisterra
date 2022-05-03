package design.screens.map.systems;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import component.camera.Focused;
import component.position.WorldPos;
import game.systems.map.MapManager;
import game.systems.render.world.RenderingSystem;
import game.systems.resources.MapSystem;
import game.utils.Colors;
import shared.model.map.Map;
import shared.model.map.Tile;
import shared.util.MapHelper;
import game.systems.render.BatchSystem;

@Wire(injectInherited = true)
public class MapDesignRenderingSystem extends RenderingSystem {

    private final MapHelper helper;
    ShapeRenderer shapeRenderer = new ShapeRenderer();
    private int current;
    private Map map;
    private MapManager mapManager;
    private boolean showExit, showBlocks, showGrid, showLayer1 = true, showLayer2= true, showLayer3=true, showLayer4= true;
    private BatchSystem batchRenderingSystem;

    public MapDesignRenderingSystem() {
        super(Aspect.all(Focused.class, WorldPos.class));
        helper = MapSystem.getHelper();
        shapeRenderer.setColor(Colors.TRANSPARENT_RED);
        shapeRenderer.setAutoShapeType(true);
    }

    public Map loadMap(int mapNumber) {
        map = helper.getMap(mapNumber);
        current = mapNumber;
        return map;
    }

    @Override
    protected void begin() {
        getCamera().update();
        batchRenderingSystem.getBatch().setProjectionMatrix(getCamera().combined);
        shapeRenderer.setProjectionMatrix(getCamera().combined);
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
                if (!showLayer1 && i == 0){
                    i++;
                }
                if (!showLayer2 && i == 1){
                    i++;
                }
                if (!showLayer3 && i == 2){
                    i++;
                }
                if (!showLayer4 && i == 3){
                    break;
                }
                batchRenderingSystem.getBatch().begin();
                mapManager.drawLayer(map, world.getDelta(), i, showExit, showBlocks);
                batchRenderingSystem.getBatch().end();
            }
            if (showGrid) {
                shapeRenderer.begin();
                float start = Tile.TILE_PIXEL_HEIGHT;
                for (int i = 0; i < map.getHeight(); i++) {
                    // draw col
                    float x = (i + 1) * Tile.TILE_PIXEL_WIDTH;
                    shapeRenderer.line(x, start, x, map.getHeight() * Tile.TILE_PIXEL_HEIGHT);
                    // draw row
                    shapeRenderer.line(start, x, map.getWidth() * Tile.TILE_PIXEL_WIDTH, x);
                }
                shapeRenderer.end();
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

    public void toggleLayer1(){
        showLayer1 = !showLayer1;
    }
    public void toggleLayer2(){
        showLayer2 = !showLayer2;
    }
    public void toggleLayer3(){
        showLayer3 = !showLayer3;
    }
    public void toggleLayer4(){
        showLayer4 = !showLayer4;
    }
}
