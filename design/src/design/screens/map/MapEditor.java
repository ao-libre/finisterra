package design.screens.map;

import com.artemis.SuperMapper;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.esotericsoftware.minlog.Log;
import com.google.common.base.Objects;
import design.screens.DesignScreen;
import design.screens.ScreenEnum;
import design.screens.ScreenManager;
import design.screens.map.gui.MapAssetChooser;
import design.screens.map.gui.MapPalette;
import design.screens.map.gui.MapPalette.Selection;
import design.screens.map.gui.MapProperties;
import design.screens.map.model.TileSet;
import design.screens.map.systems.MapDesignRenderingSystem;
import design.screens.views.TileSetView;
import design.utils.FileUtils;
import game.handlers.AnimationHandler;
import game.handlers.DescriptorHandler;
import game.handlers.ObjectHandler;
import game.managers.MapManager;
import game.systems.camera.CameraSystem;
import launcher.DesignCenter;
import position.Pos2D;
import position.WorldPos;
import shared.model.map.Map;
import shared.model.map.Tile;
import shared.model.map.WorldPosition;
import shared.util.AOJson;
import shared.util.MapHelper;
import shared.util.Pair;
import shared.util.Util;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Optional;

import static com.artemis.E.E;
import static launcher.DesignCenter.SKIN;

public class MapEditor extends DesignScreen {

    private final Stage stage;
    private World world;
    private int viewer;

    // state
    private boolean dragging;

    private MapAssetChooser assetChooser;
    private MapProperties mapProperties;
    private MapPalette mapPalette;
    private Deque<Undo> undoableActions = new ArrayDeque<>(50);
    private Table menu;
    private Pair<WorldPos, WorldPos> tilesSelection;
    private WorldPos origin;

    public MapEditor() {
        stage = new Stage() {

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
                    float x = Gdx.input.getDeltaX();
                    float y = Gdx.input.getDeltaY();
                    world.getSystem(CameraSystem.class).camera.translate(-x, -y);
                    Vector3 position = world.getSystem(CameraSystem.class).camera.position;
                    position.x = MathUtils.clamp(position.x, 0, 64000);
                    position.y = MathUtils.clamp(position.y, 0, 64000);
                } else {
                    dragging = true;
                    Selection selection = mapPalette.getSelection();
                    if (selection != Selection.SELECTION) {
                        setTile();
                    } else {
                        mouseToWorldPos().ifPresent(pos -> {
                            tilesSelection = new Pair(origin, pos);
                            world.getSystem(MapDesignRenderingSystem.class).setTilesSelection(tilesSelection);
                        });
                    }
                }
                return true;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                boolean b = super.touchDown(screenX, screenY, pointer, button);
                Optional<WorldPos> worldPos = mouseToWorldPos();
                worldPos.ifPresent(pos -> origin = pos);
                return b;
            }

            @Override
            public boolean scrolled(int amount) {
                boolean result = super.scrolled(amount);
                if (isOverGUI()) {
                    return result;
                }
                if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)) {
                    CameraSystem system = world.getSystem(CameraSystem.class);
                    system.zoom(amount, CameraSystem.ZOOM_TIME);
                } else {
                    int x = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT) ? amount : 0;
                    int y = !(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
                            Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) ?
                            amount : 0;
                    x *= 70;
                    y *= 70;
                    world.getSystem(CameraSystem.class).camera.translate(x, y);
                }
                return result;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                Selection selection = mapPalette.getSelection();
                if (selection == Selection.SELECTION && dragging && origin != null) {
                    mouseToWorldPos().ifPresent(pos -> {
                        tilesSelection = new Pair(origin, pos);
                        world.getSystem(MapDesignRenderingSystem.class).setTilesSelection(tilesSelection);
                    });
                    origin = null;
                } else if(tilesSelection != null && !dragging && !isOverGUI()) {
                    world.getSystem(MapDesignRenderingSystem.class).setTilesSelection(null);
                    tilesSelection = null;
                } else {
                    setTile();
                }
                dragging = false;
                return super.touchUp(screenX, screenY, pointer, button);
            }

            void setTile() {
                if (!isOverGUI()) {
                    mouseToWorldPos().ifPresent(MapEditor.this::setTile);
                }
            }

            @Override
            public boolean keyUp(int keyCode) {
                switch (keyCode) {
                    case Input.Keys.Z:
                        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) ||
                                Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)) {
                            if (undoableActions.size() > 0) {
                                Undo poll = undoableActions.pop();
                                Map current = mapProperties.getCurrent();
                                current.setTile(poll.pos.x, poll.pos.y, poll.tile);
                            }
                        }
                        break;
                }
                return super.keyUp(keyCode);
            }
        };
        Gdx.input.setInputProcessor(stage);
        createUI();
        createWorld();
    }

    private boolean isOverGUI() {
        int x = Gdx.app.getInput().getX();
        int y = Gdx.app.getInput().getY();
        Actor hit = menu.hit(x, y, true);
        boolean overGUI = mapPalette.isOver() || mapProperties.isOver() || assetChooser.isOver() || hit != null;
        Log.info("Touching GUI: " + overGUI);
        return overGUI;
    }

    private void setTile(int x, int y) {
        setTile(new WorldPos(x, y, 0));
    }

    private void setTile(WorldPos pos) {
        if (isOverGUI()) {
            return;
        }
        doSetTile(pos);
    }

    private void doSetTile(WorldPos pos) {
        Map map = mapProperties.getCurrent();
        Tile tile = map.getTile(pos.x, pos.y);
        Undo undo = new Undo(new Tile(tile), pos);
        Undo last = undoableActions.peek();
        if (last != null && dragging && undo.pos.equals(last.pos)) {
            return;
        }
        boolean saveUndo = true;
        Selection selection = mapPalette.getSelection();
        int layer = mapPalette.getLayer();
        switch (selection) {
            case NONE:
                switch (layer) {
                    case 0:
                    case 2:
                    case 3:
                        if (assetChooser.getImage() > 0) {
                            tile.getGraphic()[layer] = assetChooser.getImage();
                        } else {
                            saveUndo = false;
                        }
                        break;
                    case 1:
                        if (assetChooser.getAnimation() > 0) {
                            tile.getGraphic()[layer] = assetChooser.getAnimation();
                        } else {
                            saveUndo = false;
                        }
                        break;
                }
                break;
            case BLOCK:
                tile.setBlocked(!tile.isBlocked());
                break;
            case CLEAN:
                tile.getGraphic()[layer] = 0;
                break;
            case TILE_SET:
                int tileset = assetChooser.getTileset();
                if (tileset > 0) {
                    TileSetView view = (TileSetView) ScreenEnum.TILE_SET_VIEW.getScreen();
                    view.getDesigner().get(tileset).ifPresent(tileSet -> {
                        for (int x = 0; x < tileSet.getCols(); x++) {
                            for (int y = 0; y < tileSet.getRows(); y++) {
                                putTileSet(pos.x + x, pos.y + y, pos.map, map, tileSet.getImage(x, y));
                            }
                        }
                    });
                    saveUndo = false;
                }
                break;
            case TILE_EXIT:
                WorldPosition tileExit = tile.getTileExit();
                WorldPosition chosenTileExit = assetChooser.getTileExit();
                WorldPosition emptyTranslate = new WorldPosition();
                if (tileExit == null || tileExit.equals(emptyTranslate)) {
                    tile.setTileExit(chosenTileExit);
                } else {
                    tile.setTileExit(emptyTranslate);
                }
                break;
        }
        if (saveUndo) {
            undoableActions.push(undo);
        }
    }

    private void putTileSet(int x, int y, int mapId, Map map, int image) {
        Tile tile = MapHelper.getTile(map, new WorldPos(x, y, mapId));
        if (tile != null) {
            undoableActions.push(new Undo(new Tile(tile), new WorldPos(x, y, mapId)));
            tile.getGraphic()[mapPalette.getLayer()] = image;
        }
    }

    private Optional<WorldPos> mouseToWorldPos() {
        CameraSystem camera = world.getSystem(CameraSystem.class);
        Vector3 screenPos = camera.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        WorldPos value = Util.toWorld(new Pos2D(screenPos.x, screenPos.y));
        if (MapHelper.getTile(mapProperties.getCurrent(), value) != null) {
            return Optional.of(value);
        }
        return Optional.empty();
    }

    @Override
    public Stage getStage() {
        return stage;
    }

    private void createWorld() {
        AnimationHandler animationHandler = ((DesignCenter) Gdx.app.getApplicationListener()).getAnimationHandler();
        DescriptorHandler descriptorHandler = ((DesignCenter) Gdx.app.getApplicationListener()).getDescriptorHandler();
        WorldConfigurationBuilder builder = new WorldConfigurationBuilder();
        builder
                .with(new SuperMapper())
                .with(new ObjectHandler())
                .with(new CameraSystem(1f, 5f))
                .with(animationHandler)
                .with(descriptorHandler)
                .with(new MapDesignRenderingSystem(new SpriteBatch()))
                .with(new MapManager());

        WorldConfiguration config = builder.build();
        world = new World(config);
        createNewMap();
        int camera = world.create();
        E(camera)
                .pos2D()
                .aOCamera()
                .worldPos();
        viewer = world.create();
        E(viewer)
                .focused()
                .worldPos();
    }

    @Override
    protected void keyPressed(int keyCode) {
    }

    @Override
    protected Table createMenuButtons() {
        menu = new Table();
        Button back = new Button(SKIN, "close");
        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ScreenManager.getInstance().showScreen(ScreenEnum.IMAGE_VIEW);
            }
        });
        menu.add(back).left().expandX();

        menu.add(createButton("Show Exits", "switch",
                () -> world.getSystem(MapDesignRenderingSystem.class).toggleExits(), "Toggle exit tiles draw"))
                .spaceLeft(5);

        menu.add(createButton("Show Blocks", "switch",
                () -> world.getSystem(MapDesignRenderingSystem.class).toggleBlocks(), "Toggle blocks draw"))
                .spaceLeft(5);

        menu.add(createButton("Show Grid", "switch",
                () -> world.getSystem(MapDesignRenderingSystem.class).toggleGrid(), "Toggle blocks draw"))
                .spaceLeft(5);

        menu.add(createButton("New", "default", () -> {
            createNewMap();
        }, "Create new empty map")).spaceLeft(5);

        menu.add(createButton("Fill", "default", () -> {
            Map current = mapProperties.getCurrent();
            int x = 1, y = 1, i = 1, j = 1, maxX, maxY;
            Selection selection = mapPalette.getSelection();
            if (selection.equals(Selection.TILE_SET)) {
                int tileset = assetChooser.getTileset();
                if (tileset > 0) {
                    TileSetView view = (TileSetView) ScreenEnum.TILE_SET_VIEW.getScreen();
                    Optional<TileSet> tileSet = view.getDesigner().get(tileset);
                    if (tileSet.isPresent()) {
                        TileSet tileSet1 = tileSet.get();
                        i = tileSet1.getCols();
                        j = tileSet1.getRows();
                    }
                }
            }
            if (tilesSelection != null) {
                WorldPos origin = tilesSelection.getKey();
                WorldPos target = tilesSelection.getValue();
                x = Math.min(origin.x, target.x);
                y = Math.min(origin.y, target.y);
                maxX = Math.max(origin.x, target.x) + 1;
                maxY = Math.max(origin.y, target.y) + 1;
            } else {
                maxX = current.getWidth();
                maxY = current.getHeight();
            }
            int initialY = y;
            while (x < maxX) {
                while (y < maxY) {
                    doSetTile(new WorldPos(x, y, 0));
                    y += j;
                }
                x += i;
                y = initialY;
            }
        }, "All tiles will be set with current configuration (layer & selection)"))
                .spaceLeft(5);

        menu.add(createButton("Load", "default",
                () -> {
                    File file = FileUtils.openDialog("Select map (.json)", "output/maps/", new String[0], "");
                    if (file != null) {
                        Map loadedMap = new AOJson().fromJson(Map.class, new FileHandle(file));
                        if (loadedMap != null) {
                            world.getSystem(MapDesignRenderingSystem.class).setMap(loadedMap);
                            mapProperties.show(loadedMap);
                        }
                    }
                }, "Load map"))
                .spaceLeft(5);

        menu.add(createButton("Save", "default",
                () -> {
                    Map current = mapProperties.getCurrent();
                    FileHandle folder = Gdx.files.local("output/maps/");
                    new AOJson().toJson(current, folder.child(current.getName() + ".json"));
                    //TODO que guarde también los mapas limitrofes
                }, "Save map in output folder"))
                .spaceLeft(5);

        return menu;
    }

    private void createNewMap() {
        Map map = new Map();
        Arrays.stream(map.getTiles()).forEach(tiles -> {
            for (int i = 0; i < tiles.length; i++) {
                tiles[i] = new Tile();
            }
        });
        world.getSystem(MapDesignRenderingSystem.class).setMap(map);
        mapProperties.show(map);
    }

    private Button createButton(String label, String style, Runnable listener, String tooltip) {
        Button button = new ImageTextButton(label, SKIN, style);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                listener.run();
            }
        });
        button.addListener(new TextTooltip(tooltip, SKIN));
        return button;
    }

    private void initMap(int map) {
        Map map1 = world.getSystem(MapDesignRenderingSystem.class).loadMap(map);
        E(viewer)
                .worldPosMap(map)
                .worldPosY(50)
                .worldPosX(50);
        mapProperties.show(map1);
    }

    @Override
    protected Table createContent() {
        Table table = new Table();
        createLeftPane(table);
        createBottomPane(table);
        createRightPane(table);
        return table;
    }

    private void createBottomPane(Table table) {
        mapProperties = new MapProperties();
        table.add(mapProperties).left().bottom().prefWidth(270).prefHeight(240).expandX();
    }

    private void createRightPane(Table table) {
        assetChooser = new MapAssetChooser();
        table.add(assetChooser).right().prefWidth(200).expandY();
        mapPalette.addListener(assetChooser);
    }

    private void createLeftPane(Table table) {
        mapPalette = new MapPalette();
        table.add(mapPalette).left().top().expandY();
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public void render(float delta) {
        if (running) {
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            if (getWorld() != null) {
                getWorld().setDelta(delta);
                getWorld().process();
            }
            getStage().act(delta);
            getStage().draw();
        }
    }

    private class Undo {
        Tile tile;
        WorldPos pos;

        Undo(Tile tile, WorldPos pos) {
            this.tile = tile;
            this.pos = pos;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Undo undo = (Undo) o;
            return Objects.equal(tile, undo.tile) &&
                    Objects.equal(pos, undo.pos);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(tile, pos);
        }
    }

}
