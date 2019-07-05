package game.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import model.readers.GenericReader;
import shared.model.Spell;
import shared.util.SpellJson;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SpellsLoader extends AsynchronousAssetLoader<HashMap<Integer, Spell>, SpellsLoader.SpellParameter<HashMap<Integer, Spell>>> {

    HashMap<Integer, Spell> result;

    public SpellsLoader() {
        super(new InternalFileHandleResolver());
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, SpellParameter parameter) {
        result = null;
        result = new HashMap<>();
        SpellJson.load(result, file);
    }

    @Override
    public HashMap<Integer, Spell> loadSync(AssetManager manager, String fileName, FileHandle file, SpellParameter parameter) {
        HashMap<Integer, Spell> result = this.result;
        this.result = null;
        return result;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, SpellParameter parameter) {
        return null;
    }

    static public class SpellParameter<T> extends AssetLoaderParameters<T> {
    }
}
