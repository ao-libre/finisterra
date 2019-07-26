package launcher;

import com.badlogic.gdx.Game;
import design.screens.ScreenEnum;
import design.screens.ScreenManager;
import game.AssetManagerHolder;
import game.handlers.AOAssetManager;
import game.handlers.DefaultAOAssetManager;

public class DesignCenter extends Game implements AssetManagerHolder {

    private AOAssetManager assetManager;

    @Override
    public void create() {
        assetManager = new DefaultAOAssetManager();
        assetManager.load();
        assetManager.getAssetManager().finishLoading();
        ScreenManager instance = ScreenManager.getInstance();
        instance.initialize(this);
        instance.showScreen(ScreenEnum.NPC_VIEW);
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
