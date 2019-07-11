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
        instance.showScreen(ScreenEnum.GRAPHIC_VIEW);
    }

    @Override
    public AOAssetManager getAssetManager() {
        return assetManager;
    }
}
