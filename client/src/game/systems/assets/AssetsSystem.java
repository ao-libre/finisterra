package game.systems.assets;

import game.handlers.DefaultAOAssetManager;
import net.mostlyoriginal.api.system.core.PassiveSystem;

public class AssetsSystem extends PassiveSystem {

    private DefaultAOAssetManager assetManager;

    public AssetsSystem(DefaultAOAssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public DefaultAOAssetManager getAssetManager() {
        return assetManager;
    }
}