package game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import game.AOGame;
import game.handlers.AOAssetManager;
import game.utils.Resources;

import static com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;

public class LoadingScreen extends AbstractScreen {

    private AOAssetManager assetManager;
    private Texture progressBar;
    private Texture progressBarKnob;
    private ProgressBar progress;
    private boolean loaded;

    public LoadingScreen() {
    }

    @Override
    protected void keyPressed(int keyCode) {

    }

    @Override
    void createContent() {
        AOGame game = (AOGame) Gdx.app.getApplicationListener();
        assetManager = game.getAssetManager();

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
        getMainTable().add(table).expand();
        assetManager.load();
    }

    @Override
    public void render(float delta) {
        AssetManager manager = this.assetManager.getAssetManager();
        if(manager.update() && !loaded) {
            // we are done loading, let's move to another screen!
            AOGame game = (AOGame) Gdx.app.getApplicationListener();
            game.toLogin();
            loaded = true;
        }
        // display loading information
        float progress = manager.getProgress();
        this.progress.setValue(progress * 100);
        super.render(delta);
    }
}
