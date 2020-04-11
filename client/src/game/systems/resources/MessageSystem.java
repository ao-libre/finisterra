package game.systems.resources;

import com.artemis.annotations.Wire;
import game.handlers.AOAssetManager;
import shared.util.Messages;

@Wire
public class MessageSystem extends AssetSystem {

    public MessageSystem(AOAssetManager assetManager) {
        super(assetManager);
    }

    public String getMessage(Messages key, String... params) {
        return getAssetManager().getMessages(key, params);
    }
}
