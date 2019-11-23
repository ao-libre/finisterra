package game.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import model.textures.AOAnimation;
import shared.util.AOJson;

import java.util.ArrayList;

public class AnimationLoader extends AsynchronousAssetLoader<ArrayList<AOAnimation>, AnimationLoader.DescriptorParameter> {

    public static final String ANIMATIONS = "animations";

    private final Json json = new AOJson();
    private ArrayList<AOAnimation> descriptors;

    public AnimationLoader() {
        super(new InternalFileHandleResolver());
        json.addClassTag(ANIMATIONS, AOAnimation.class);
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, DescriptorParameter parameter) {
        descriptors = null;
        descriptors = json.fromJson(ArrayList.class, AOAnimation.class, file);
    }

    @Override
    public ArrayList<AOAnimation> loadSync(AssetManager manager, String fileName, FileHandle file, DescriptorParameter parameter) {
        ArrayList<AOAnimation> synchronizedDescriptors = this.descriptors;
        this.descriptors = null;
        return synchronizedDescriptors;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, DescriptorParameter parameter) {
        return null;
    }


    static public class DescriptorParameter extends AssetLoaderParameters<ArrayList<AOAnimation>> {
    }
}
