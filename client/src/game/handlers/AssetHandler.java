package game.handlers;

import com.badlogic.gdx.Gdx;

public class AssetHandler {

    private static HandlerState state = HandlerState.UNLOADED;

    public static void load() {
        // TODO: Needs refactoring
        // Load resources
        Gdx.app.log("Loading", "Loading descriptors...");
        DescriptorHandler.load();
        Gdx.app.log("Loading", "Loading animations...");
        AnimationHandler.load();
        Gdx.app.log("Loading", "Loading objects...");
        ObjectHandler.load();
        Gdx.app.log("Loading", "Loading spells...");
        SpellHandler.load();
        Gdx.app.log("Loading", "Loading particles...");
        ParticlesHandler.load();
        Gdx.app.log("Loading", "Finish loading");

        state = HandlerState.LOADED;
    }

    public static void unload() {
        // TODO: Implement this
    }

    public static HandlerState getState() {
        return state;
    }
}
