package game.screens;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.PerformanceCounter;
import com.badlogic.gdx.utils.TimeUtils;
import com.esotericsoftware.minlog.Log;
import game.handlers.DefaultAOAssetManager;
import game.ui.WidgetFactory;
import game.utils.Resources;
import game.utils.Skins;

import java.util.function.Consumer;

import static com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;

@Wire
public class LoadingScreen extends ScreenAdapter {

    private static final Skin SKIN = Skins.CURRENT.get();
    private static final Texture BACKGROUND_TEXTURE = new Texture(Gdx.files.internal(Resources.GAME_IMAGES_PATH + "background.jpg"));
    private static final SpriteDrawable BACKGROUND = new SpriteDrawable(new Sprite(BACKGROUND_TEXTURE));
    private final Stage stage;
    private DefaultAOAssetManager assetManager;
    private Table mainTable;

    private Texture progressBar;
    private Texture progressBarKnob;
    private ProgressBar progress;
    private boolean loaded;
    private boolean textureLoading;
    private Consumer<DefaultAOAssetManager> onFinished;
    private final static float nano2seconds = 1f / 1000000000.0f;
    private float start;

    public LoadingScreen(DefaultAOAssetManager assetManager) {
        this.assetManager = assetManager;
        stage = new Stage();
        mainTable = new Table(SKIN);
        mainTable.setFillParent(true);
        mainTable.setBackground(BACKGROUND);
        stage.addActor(mainTable);
        createUI();
    }

    protected void createUI() {
        String progressBarPath = Resources.GAME_IMAGES_PATH + "progress-bar.png";
        String progressBarKnobPath = Resources.GAME_IMAGES_PATH + "progress-bar-knob.png";
        assetManager.getAssetManager().load(progressBarPath, Texture.class);
        assetManager.getAssetManager().load(progressBarKnobPath, Texture.class);
        assetManager.getAssetManager().finishLoading();

        progressBar = assetManager.getAssetManager().get(progressBarPath);
        progressBarKnob = assetManager.getAssetManager().get(progressBarKnobPath);

        Table table = new Table();
        ProgressBarStyle style = new ProgressBarStyle();
        style.background = new SpriteDrawable(new Sprite(progressBar));
        style.knob = new SpriteDrawable(new Sprite(progressBarKnob));
        progress = WidgetFactory.createLoadingProgressBar();
        table.add(progress).expandX();
        mainTable.add(table).expand();
        start = TimeUtils.nanoTime();
        assetManager.load();
    }

    @Override
    public void render(float delta) {
        AssetManager manager = this.assetManager.getAssetManager();
        if (manager.update() && !loaded) {
            if (!textureLoading) {
                textureLoading = true;
                // TODO

            } else {
                Log.info("Loading time " + (TimeUtils.nanoTime() - start) * nano2seconds + "s");
                loaded = true;
                onFinished.accept(assetManager);
            }
        }
        // display loading information
        float varProgress = manager.getProgress();
        this.progress.setValue(varProgress * 100);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void hide() {
        stage.dispose();
    }

    public void onFinished(Consumer<DefaultAOAssetManager> onFinished) {
        this.onFinished = onFinished;
    }
}
