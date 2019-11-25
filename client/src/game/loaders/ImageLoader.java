package game.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import model.textures.AOImage;
import shared.util.AOJson;

import java.util.ArrayList;

public class ImageLoader extends AsynchronousAssetLoader<ArrayList<AOImage>, ImageLoader.DescriptorParameter> {

    public static final String GAME_DESCRIPTORS_FOLDER = "data/descriptors/";
    public static final String JSON_EXTENSION = ".json";
    public static final String GRAPHICS = "graphics";
    public static final String BODIES = "bodies";
    public static final String WEAPONS = "weapons";
    public static final String SHIELDS = "shields";
    public static final String HEADS = "heads";
    public static final String HELMETS = "helmets";
    public static final String FXS = "fxs";
    public static final String IMAGES = "images";
    public static final String ANIMATIONS = "animations";

    private final Json json = new AOJson();
    private ArrayList<AOImage> descriptors;

    public ImageLoader() {
        super(new InternalFileHandleResolver());
        json.addClassTag(IMAGES, AOImage.class);
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, DescriptorParameter parameter) {
        this.descriptors = null;
        descriptors = json.fromJson(ArrayList.class, AOImage.class, file);
    }

    @Override
    public ArrayList<AOImage> loadSync(AssetManager manager, String fileName, FileHandle file, DescriptorParameter parameter) {
        ArrayList<AOImage> syncronizedDescriptors = this.descriptors;
        this.descriptors = null;
        return syncronizedDescriptors;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, DescriptorParameter parameter) {
        return null;
    }


    static public class DescriptorParameter extends AssetLoaderParameters<ArrayList<AOImage>> {
    }
}
