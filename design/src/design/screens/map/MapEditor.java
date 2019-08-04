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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.esotericsoftware.minlog.Log;
import com.google.common.base.Objects;
import design.screens.DesignScreen;
import design.screens.ScreenEnum;
import design.screens.ScreenManager;
import design.screens.map.gui.MapPalette;
import design.screens.map.gui.MapProperties;
import design.screens.map.systems.MapDesignRenderingSystem;
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
import shared.util.AOJson;
import shared.util.MapHelper;
import shared.util.Util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

import static com.artemis.E.E;
import static launcher.DesignCenter.SKIN;

public class MapEditor extends DesignScreen {

    private final Stage stage;
    private World world;
    private int viewer;

    // palette
    private int layer;
    private boolean block;
    private boolean delete;

    private MapPalette palette;
    private MapProperties mapProperties;

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

    private Deque<Undo> undoableActions = new ArrayDeque<>(50);



    public MapEditor() {
        stage = new Stage() {

            private boolean dragging;

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
                int x = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT) ? amount : 0;
                int y = !(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
                        Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) ?
                        amount : 0;
                x *= 2;
                y *= 2;
                world.getSystem(CameraSystem.class).camera.translate(x, y);
                return super.scrolled(amount);
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                setTile();
                dragging = false;
                return super.touchUp(screenX, screenY, pointer, button);
            }

            public void setTile() {
                mouseToWorldPos().ifPresent(pos -> {
                    Map map = mapProperties.getCurrent();
                    Tile tile = map.getTile(pos.x, pos.y);
                    Undo undo = new Undo(new Tile(tile), pos);
                    Undo last = undoableActions.peek();
                    boolean validAction = true;
                    if (block) {
                        tile.setBlocked(!tile.isBlocked());
                    } else if (delete) {
                        tile.getGraphic()[layer] = 0;
                    } else {
                        switch (layer) {
                            case 0:
                            case 2:
                            case 3:
                                if (palette.getImage() > 0) {
                                    tile.getGraphic()[layer] = palette.getImage();
                                } else {
                                    validAction = false;
                                }
                                break;
                            case 1:
                                if (palette.getAnimation() > 0) {
                                    tile.getGraphic()[layer] = palette.getAnimation();
                                } else {
                                    validAction = false;
                                }
                                break;
                        }
                    }

                    if (validAction && (last == null || !(dragging && undo.pos.equals(last.pos)))) {
                        Log.info("Save tile action. Tile: " + undo.tile.toString() + " in pos: " + undo.pos);
                        undoableActions.push(undo);
                    }
                });
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
                .with(new CameraSystem(1))
                .with(animationHandler)
                .with(descriptorHandler)
                .with(new MapDesignRenderingSystem(new SpriteBatch()))
                .with(new MapManager());

        WorldConfiguration config = builder.build();
        world = new World(config);
        int camera = world.create();
        E(camera).pos2D().aOCamera();
        viewer = world.create();
        E(viewer).focused();
        initMap(1);
    }

    @Override
    protected void keyPressed(int keyCode) {
        MapDesignRenderingSystem mapSystem = world.getSystem(MapDesignRenderingSystem.class);
        mapSystem.getCurrent();

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

        Button newMap = new ImageTextButton("New", SKIN);
        newMap.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Map map = new Map();
                world.getSystem(MapDesignRenderingSystem.class).addMap(map);
                mapProperties.show(map);
            }
        });
        menus.add(newMap);
        Button loadMap = new ImageTextButton("Load", SKIN);
        loadMap.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int map = 1;
                initMap(map);
            }
        });
        menus.add(loadMap).spaceLeft(5);

        Button save = new ImageTextButton("Save", SKIN);
        save.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Map current = mapProperties.getCurrent();
                FileHandle folder = Gdx.files.local("output/maps/");
                new AOJson().toJson(current, folder.child(current.getName() + ".json"));
            }
        });
        menus.add(save).spaceLeft(5);

        return menus;
    }

    public void initMap(int map) {
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
        table.setDebug(true);
        createLeftPane(table);
        createBottomPane(table);
        createRightPane(table);
        return table;
    }

    private void createBottomPane(Table table) {
        mapProperties = new MapProperties();
        table.add(mapProperties).bottom().prefWidth(450).expandX();
    }

    private void createRightPane(Table table) {
        palette = new MapPalette();
        table.add(palette).right().width(200).expandY();
    }

    private void createLeftPane(Table table) {
        Window leftPane = new Window("Layers", SKIN, "main");
        leftPane.setMovable(true);
        ButtonGroup group = new ButtonGroup();
        Button fst = new TextButton("1", SKIN, "file");
        fst.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                layer = 0;
            }
        });
        leftPane.add(fst).spaceTop(5).growX().row();
        Button snd = new TextButton("2", SKIN, "file");
        snd.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                layer = 1;
            }
        });
        leftPane.add(snd).growX().row();
        Button third = new TextButton("3", SKIN, "file");
        third.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                layer = 2;
            }
        });
        leftPane.add(third).growX().row();
        Button forth = new TextButton("4", SKIN, "file");
        forth.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                layer = 3;
            }
        });
        leftPane.add(forth).growX().row();
        group.add(fst, snd, third, forth);

        Button block = new TextButton("block", SKIN, "file");
        block.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MapEditor.this.block = block.isChecked();
            }
        });
        leftPane.add(block).spaceTop(10).row();
        Button clean = new Button(SKIN, "delete-check");
        clean.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MapEditor.this.delete = clean.isChecked();
            }
        });
        leftPane.add(clean).spaceTop(10).spaceBottom(5).row();

        table.add(leftPane).left().expandY();
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
}
