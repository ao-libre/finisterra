package design.screens.map.systems;

import camera.Focused;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import game.handlers.MapHandler;
import game.managers.MapManager;
import game.systems.render.world.RenderingSystem;
import game.utils.Colors;
import position.WorldPos;
import shared.model.map.Map;
import shared.model.map.Tile;
import shared.util.MapHelper;
import shared.util.Pair;

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
    private Pair<WorldPos, WorldPos> tilesSelection;

    public MapDesignRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(Focused.class, WorldPos.class), batch, CameraKind.WORLD);
        helper = MapHandler.getHelper();

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
        getBatch().setProjectionMatrix(getCamera().combined);
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
                getBatch().begin();
                mapManager.drawLayer(map, getBatch(), world.getDelta(), i, showExit, showBlocks);
                getBatch().end();
            }
            if (showGrid) {
                sr.begin();
                sr.setColor(Colors.TRANSPARENT_RED);
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
            if (tilesSelection != null) {
                drawSelection();
            }
        }
        // draw tiles lines
    }

    private void drawSelection() {
        sr.begin();
        sr.setColor(Colors.CITIZEN);
        WorldPos origin = tilesSelection.getKey();
        WorldPos target = tilesSelection.getValue();
        float minX = Math.min(origin.x, target.x) * Tile.TILE_PIXEL_WIDTH;
        float minY = Math.min(origin.y, target.y) * Tile.TILE_PIXEL_HEIGHT;

        float maxX = Math.max(origin.x, target.x) * Tile.TILE_PIXEL_WIDTH;
        float maxY = Math.max(origin.y, target.y) * Tile.TILE_PIXEL_HEIGHT;
        sr.rect(minX, minY, (maxX - minX) + Tile.TILE_PIXEL_WIDTH, (maxY - minY) + Tile.TILE_PIXEL_HEIGHT);
        sr.end();
    }

    public void setTilesSelection(Pair<WorldPos, WorldPos> tilesSelection) {
        this.tilesSelection = tilesSelection;
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
