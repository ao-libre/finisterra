package launcher;

import com.artemis.SuperMapper;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowListener;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.minlog.Log;
import design.screens.ScreenEnum;
import design.screens.ScreenManager;
import design.screens.views.View;
import game.AssetManagerHolder;
import game.ClientConfiguration;
import game.handlers.*;
import game.screens.WorldScreen;
import game.systems.resources.AnimationsSystem;
import game.systems.resources.DescriptorsSystem;
import game.systems.resources.ObjectSystem;
import game.utils.Skins;
import design.graphic.AnimationDrawable;
import shared.util.LogSystem;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DesignCenter extends Game implements AssetManagerHolder, WorldScreen, Lwjgl3WindowListener {

    public static Skin SKIN;
    private AOAssetManager assetManager;
    private AnimationsSystem animationsSystem;
    private DescriptorsSystem descriptorsSystem;
    private World world;
    private AnimationDrawable loadingAnimation;
    private boolean loaded;
    private Stage stage;

    @Override
    public void create() {
        Log.setLogger(new LogSystem());
        SKIN = new Skins.AOSkin(Gdx.files.internal("skin/skin-composer-ui.json"));
        loadingAnimation = new AnimationDrawable(SKIN, "loading-animation", 1 / 30f);
        assetManager = new DefaultAOAssetManager(ClientConfiguration.createConfig());
        assetManager.load();
        Table t = new Table();
        t.setFillParent(true);
        Label label = new Label("Loading...", SKIN, "title");
        label.setAlignment(Align.center);
        t.add(label);
        t.row();
        Table table = new Table(SKIN);
        table.setBackground(loadingAnimation);
        t.setBackground(SKIN.getDrawable("white"));
        t.add(table);
        stage = new Stage(new ScreenViewport());
        stage.addActor(t);
        Gdx.input.setInputProcessor(stage);
    }

    public void show() {
        world = createWorld();
        ScreenManager instance = ScreenManager.getInstance();
        instance.initialize(this);
        instance.showScreen(ScreenEnum.IMAGE_VIEW);
    }

    @Override
    public void render() {
        super.render();
        if (!loaded) {
            float delta = Gdx.graphics.getDeltaTime();
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            stage.act(delta);
            stage.draw();
            if (assetManager.getAssetManager().update()) {
                loaded = true;
                show();
            } else {
                loadingAnimation.update(Gdx.graphics.getDeltaTime());
            }
        }
    }

    private World createWorld() {
        WorldConfigurationBuilder builder = new WorldConfigurationBuilder();
        builder
                .with(new SuperMapper())
                .with(new ObjectSystem())
                .with(animationsSystem = new AnimationsSystem())
                .with(descriptorsSystem = new DescriptorsSystem());
        WorldConfiguration config = builder.build();
        config.register(assetManager);
        return new World(config);
    }

    public DescriptorsSystem getDescriptorsSystem() {
        return descriptorsSystem;
    }

    public AnimationsSystem getAnimationsSystem() {
        return animationsSystem;
    }

    @Override
    public AOAssetManager getAssetManager() {
        return assetManager;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public void resize(int width, int height) {
        // See below for what true means.
        stage.getViewport().update(width, height, true);
        Screen varScreen = getScreen();
        if (varScreen instanceof View) {
            ((View) varScreen).update(width, height);
        }
    }

    @Override
    public void created(Lwjgl3Window window) {

    }

    @Override
    public void iconified(boolean isIconified) {

    }

    @Override
    public void maximized(boolean isMaximized) {

    }

    @Override
    public void focusLost() {

    }

    @Override
    public void focusGained() {

    }

    @Override
    public boolean closeRequested() {
        // no tengo idea de porque lo pusieron como false por esto era que no se podia cerrar
        return true;
    }

    @Override
    public void filesDropped(String[] files) {
        List<FileHandle> list = Arrays.stream(files).map(FileHandle::new).collect(Collectors.toList());
        Screen varScreen = getScreen();
        if (varScreen instanceof View) {
            ((View) varScreen).filesDropped(list);
        }
    }

    @Override
    public void refreshRequested() {

    }

    @Override
    public void dispose() {
        super.dispose();
        Log.info("Saliendo de Finisterra Design Center ");
        // como no se si el super.dispose() cubre lo de abajo tambien lo puse
        screen.dispose();
        SKIN.dispose();
        world.dispose();
        stage.dispose();
        assetManager.dispose();
        // esta si se que hay que ponerla para al final de un programa que use Gdx
        Gdx.app.exit();
        System.exit(0);
    }
}
