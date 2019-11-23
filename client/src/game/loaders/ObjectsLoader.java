package game.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import shared.objects.types.Obj;
import shared.objects.types.Type;
import shared.util.ObjJson;

import java.util.HashMap;

public class ObjectsLoader extends AsynchronousAssetLoader<HashMap<Integer, Obj>, ObjectsLoader.ObjectParameter<HashMap<Integer, Obj>>> {

    HashMap<Integer, Obj> result;
    ObjJson json = new ObjJson();

    public ObjectsLoader() {
        super(new InternalFileHandleResolver());
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, ObjectParameter parameter) {
        result = null;
        result = new HashMap<>();
        ObjJson.loadObjs(result, file, parameter.type, json);
    }

    @Override
    public HashMap<Integer, Obj> loadSync(AssetManager manager, String fileName, FileHandle file, ObjectParameter parameter) {
        HashMap<Integer, Obj> synchronizedResult = this.result;
        this.result = null;
        return synchronizedResult;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, ObjectParameter parameter) {
        return null;
    }

    public static class ObjectParameter<T> extends AssetLoaderParameters<T> {
        Type type;

        public ObjectParameter(Type type) {
            this.type = type;
        }
    }
}
