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
import game.ClientConfiguration;
import game.handlers.DefaultAOAssetManager;
import game.utils.Resources;
import game.utils.Skins;

import java.util.function.Consumer;

import static com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;

@Wire
public class LoadingScreen extends ScreenAdapter {

    private DefaultAOAssetManager assetManager;

    private static final Skin SKIN = Skins.COMODORE_SKIN;
    private static final Texture BACKGROUND_TEXTURE = new Texture(Gdx.files.internal(Resources.GAME_IMAGES_PATH + "background.jpg"));
    private static final SpriteDrawable BACKGROUND = new SpriteDrawable(new Sprite(BACKGROUND_TEXTURE));

    private final Stage stage;
    private Table mainTable;

    private Texture progressBar;
    private Texture progressBarKnob;
    private ProgressBar progress;
    private boolean loaded;
    private boolean textureLoading;
    private Consumer<DefaultAOAssetManager> onFinished;

    public LoadingScreen(ClientConfiguration clientConfiguration) {
        this.assetManager = new DefaultAOAssetManager(clientConfiguration);
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
        progress = new ProgressBar(1, 100, 1, false, style);
        table.add(progress).expandX();
        mainTable.add(table).expand();
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
                loaded = true;
                onFinished.accept(assetManager);
            }
        }
        // display loading information
        float varProgress = manager.getProgress();
        this.progress.setValue(varProgress * 100);
        super.render(delta);
    }

    public void onFinished(Consumer<DefaultAOAssetManager> onFinished) {
        this.onFinished = onFinished;
    }
}
