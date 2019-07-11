package launcher;

import com.artemis.SuperMapper;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import design.screens.ScreenEnum;
import design.screens.ScreenManager;
import design.screens.views.View;
import game.AssetManagerHolder;
import game.handlers.*;
import game.screens.WorldScreen;
import game.utils.Skins;
import graphics.AnimationDrawable;

public class DesignCenter extends Game implements AssetManagerHolder, WorldScreen {

    public static Skin SKIN;
    private AOAssetManager assetManager;
    private AnimationHandler animationHandler;
    private DescriptorHandler descriptorHandler;
    private World world;
    private AnimationDrawable loadingAnimation;
    private boolean loaded;
    private Stage stage;

    @Override
    public void create() {
        SKIN = new Skins.AOSkin(Gdx.files.internal("skin/skin-composer-ui.json"));
        loadingAnimation = new AnimationDrawable(SKIN, "loading-animation", 1 / 30f);
        assetManager = new DefaultAOAssetManager();
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

    protected World createWorld() {
        WorldConfigurationBuilder builder = new WorldConfigurationBuilder();
        builder
                .with(new SuperMapper())
                .with(new ObjectHandler())
                .with(animationHandler = new AnimationHandler())
                .with(descriptorHandler = new DescriptorHandler());
        WorldConfiguration config = builder.build();
        return new World(config);
    }

    public DescriptorHandler getDescriptorHandler() {
        return descriptorHandler;
    }

    public AnimationHandler getAnimationHandler() {
        return animationHandler;
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
    public void resize (int width, int height) {
        // See below for what true means.
        stage.getViewport().update(width, height, true);
        Screen screen = getScreen();
        if (screen instanceof View) {
            ((View) screen).update(width, height);
        }
    }
}