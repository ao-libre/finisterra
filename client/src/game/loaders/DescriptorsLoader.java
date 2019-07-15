package game.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import model.descriptors.*;
import model.textures.AOAnimation;
import model.textures.AOImage;
import shared.model.Graphic;
import shared.util.AOJson;

import java.util.ArrayList;

public class DescriptorsLoader extends AsynchronousAssetLoader<ArrayList<Descriptor>, DescriptorsLoader.DescriptorParameter> {

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

    private Json json = new AOJson();
    private ArrayList<Descriptor> descriptors;

    public DescriptorsLoader() {
        super(new InternalFileHandleResolver());
        json.addClassTag(GRAPHICS, Graphic.class);
        json.addClassTag(IMAGES, AOImage.class);
        json.addClassTag(ANIMATIONS, AOAnimation.class);
        json.addClassTag(BODIES, BodyDescriptor.class);
        json.addClassTag(HEADS, HeadDescriptor.class);
        json.addClassTag(HELMETS, HelmetDescriptor.class);
        json.addClassTag(WEAPONS, WeaponDescriptor.class);
        json.addClassTag(SHIELDS, ShieldDescriptor.class);
        json.addClassTag(FXS, FXDescriptor.class);
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, DescriptorParameter parameter) {
        descriptors = null;
        descriptors = json.fromJson(ArrayList.class, parameter.clazz, file);
    }

    @Override
    public ArrayList<Descriptor> loadSync(AssetManager manager, String fileName, FileHandle file, DescriptorParameter parameter) {
        ArrayList<Descriptor> descriptors = this.descriptors;
        this.descriptors = null;
        return descriptors;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, DescriptorParameter parameter) {
        return null;
    }


    static public class DescriptorParameter extends AssetLoaderParameters<ArrayList<Descriptor>> {
        final Class clazz;

        private DescriptorParameter(Class clazz) {
            this.clazz = clazz;
        }

        public static DescriptorParameter descriptor(Class clazz) {
            return new DescriptorParameter(clazz);
        }
    }
}
