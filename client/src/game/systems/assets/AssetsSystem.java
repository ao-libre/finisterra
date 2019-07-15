package game.systems.assets;

import game.handlers.AOAssetManager;
import net.mostlyoriginal.api.system.core.PassiveSystem;

public class AssetsSystem extends PassiveSystem {

    private AOAssetManager assetManager;

    public AssetsSystem(AOAssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public AOAssetManager getAssetManager() {
        return assetManager;
    }
}