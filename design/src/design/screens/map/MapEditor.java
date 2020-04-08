package design.screens.map;

import com.artemis.SuperMapper;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.google.common.base.Objects;
import design.screens.DesignScreen;
import design.screens.ScreenEnum;
import design.screens.ScreenManager;
import design.screens.map.gui.MapAssetChooser;
import design.screens.map.gui.MapPalette;
import design.screens.map.gui.MapPalette.Selection;
import design.screens.map.gui.MapProperties;
import design.screens.map.systems.MapDesignRenderingSystem;
import design.screens.views.TileSetView;
import game.systems.camera.CameraSystem;
import game.systems.map.MapManager;
import game.systems.render.BatchRenderingSystem;
import game.systems.resources.AnimationsSystem;
import game.systems.resources.DescriptorsSystem;
import game.systems.resources.ObjectSystem;
import launcher.DesignCenter;
import component.position.WorldPos;
import component.position.WorldPosOffsets;
import shared.model.map.Map;
import shared.model.map.Tile;
import shared.model.map.WorldPosition;
import shared.util.AOJson;
import shared.util.MapHelper;
import shared.util.WorldPosConversion;

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

    public MapEditor() {
        stage = new Stage() {

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
                    float x = Gdx.input.getDeltaX();
                    float y = Gdx.input.getDeltaY();
                    world.getSystem(CameraSystem.class).camera.translate(-x, -y);
                } else {
                    dragging = true;
                    setTile();
                }
                return true;
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
                setTile();
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
        return mapPalette.isOver() || mapProperties.isOver() || assetChooser.isOver();
    }

    private void setTile(int x, int y) {
        setTile(new WorldPos(x, y, 0));
    }

    private void setTile(WorldPos pos) {
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
        WorldPos value = WorldPosConversion.toWorld(new WorldPosOffsets(screenPos.x, screenPos.y));
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
        AnimationsSystem animationsSystem = ((DesignCenter) Gdx.app.getApplicationListener()).getAnimationsSystem();
        DescriptorsSystem descriptorsSystem = ((DesignCenter) Gdx.app.getApplicationListener()).getDescriptorsSystem();
        WorldConfigurationBuilder builder = new WorldConfigurationBuilder();
        builder
                .with(new SuperMapper())
                .with(new ObjectSystem())
                .with(new CameraSystem(0.1f, 2f))
                .with(animationsSystem)
                .with(descriptorsSystem)
                .with(new MapDesignRenderingSystem())
                .with(new BatchRenderingSystem())
                .with(new MapManager());

        WorldConfiguration config = builder.build();
        world = new World(config);
        int camera = world.create();
        E(camera).worldPosOffsets().aOCamera();
        viewer = world.create();
        E(viewer).focused();
        initMap(1);
    }

    @Override
    protected void keyPressed(int keyCode) {
    }

    @Override
    protected Table createMenuButtons() {
        Table menus = new Table();
        Button back = new Button(SKIN, "close");
        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ScreenManager.getInstance().showScreen(ScreenEnum.IMAGE_VIEW);
            }
        });
        menus.add(back).left().expandX();

        menus.add(createButton("Show Exits", "switch",
                () -> world.getSystem(MapDesignRenderingSystem.class).toggleExits(), "Toggle exit tiles draw"))
                .spaceLeft(5);

        menus.add(createButton("Show Blocks", "switch",
                () -> world.getSystem(MapDesignRenderingSystem.class).toggleBlocks(), "Toggle blocks draw"))
                .spaceLeft(5);

        menus.add(createButton("Show Grid", "switch",
                () -> world.getSystem(MapDesignRenderingSystem.class).toggleGrid(), "Toggle blocks draw"))
                .spaceLeft(5);

        menus.add(createButton("New", "default", () -> {
            Map map = new Map();
            Arrays.stream(map.getTiles()).forEach(tiles -> {
                for (int i = 0; i < tiles.length; i++) {
                    tiles[i] = new Tile();
                }
            });
            world.getSystem(MapDesignRenderingSystem.class).setMap(map);
            mapProperties.show(map);
        }, "Create new empty map")).spaceLeft(5);

        menus.add(createButton("Fill", "default", () -> {
            Map current = mapProperties.getCurrent();
            for (int i = 0; i < current.getWidth(); i++) {
                for (int j = 0; j < current.getHeight(); j++) {
                    setTile(i, j);
                }
            }
        }, "All tiles will be set with current configuration (layer & selection)"))
                .spaceLeft(5);

        menus.add(createButton("Load", "default",
                () -> {
                    int map = 1;
                    initMap(map);
                }, "Load map"))
                .spaceLeft(5);

        menus.add(createButton("Save", "default",
                () -> {
                    Map current = mapProperties.getCurrent();
                    FileHandle folder = Gdx.files.local("output/maps/");
                    new AOJson().toJson(current, folder.child(current.getName() + ".json"));
                    //TODO que guarde también los mapas limitrofes
                }, "Save map in output folder"))
                .spaceLeft(5);

        return menus;
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
        table.add(assetChooser).right().width(200).expandY();
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
