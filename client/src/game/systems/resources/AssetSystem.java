package game.systems.resources;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import game.handlers.AOAssetManager;

@Wire
public class AssetSystem extends BaseSystem {

    private final AOAssetManager assetManager;

    public AssetSystem(AOAssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public AOAssetManager getAssetManager() {
        return assetManager;
    }

    @Override
    protected void processSystem() {

    }
}
