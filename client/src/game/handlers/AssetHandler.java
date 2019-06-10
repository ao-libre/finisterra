package game.handlers;

import com.badlogic.gdx.Gdx;
import org.lwjgl.openal.AL;

public class AssetHandler {

    private static StateHandler state = StateHandler.UNLOADED;

    public static void load() {
        // TODO: Needs refactoring
        // Load resources
        new Thread(() -> {
            Gdx.app.log("Loading", "Loading world...");
            MapHandler.getHelper();
            Gdx.app.log("Loading", "Loading descriptors...");
            DescriptorHandler.load();
            Gdx.app.log("Loading", "Loading objects...");
            ObjectHandler.load();
            Gdx.app.log("Loading", "Loading spells...");
            SpellHandler.load();
        }).start();
        Gdx.app.log("Loading", "Loading graphics...");
        SurfaceHandler.loadAllTextures();
        Gdx.app.log("Loading", "Loading animations...");
        AnimationHandler.load();
        Gdx.app.log("Loading", "Loading particles...");
        ParticlesHandler.load();
        Gdx.app.log("Loading", "Loading Sounds...");
        SoundsHandler.load();
        Gdx.app.log("Loading", "Loading Music...");
        MusicHandler.load();
        Gdx.app.log("Loading", "Finish loading");

        state = StateHandler.LOADED;
    }

    public static void unload() {
        MusicHandler.unload();
        SoundsHandler.unload();
    }

    public static StateHandler getState() {
        return state;
    }
}
