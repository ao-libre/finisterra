package game.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import model.descriptors.BodyDescriptor;
import model.readers.GenericReader;
import model.serializers.BodyDescriptorSerializer;
import shared.model.Graphic;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class GenericLoader<T> extends AsynchronousAssetLoader<HashMap<Integer, T>, GenericLoader.GenericParameter<T>> {

    private HashMap<Integer, T> result;
    private GenericReader<T> genericReader = new GenericReader<>();

    public GenericLoader() {
        super(new InternalFileHandleResolver());
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, GenericParameter parameter) {
        result = null;
        result = (HashMap) genericReader.read(file, parameter.getClazz(), parameter.getSerializer(), parameter.getFunction());
    }

    @Override
    public HashMap<Integer, T> loadSync(AssetManager manager, String fileName, FileHandle file, GenericParameter parameter) {
        HashMap<Integer, T> result = this.result;
        if (parameter.clazz == Graphic.class) {
            Map<Integer, Graphic> graphics = (Map<Integer, Graphic>) result;
//            TODO graphics.forEach(animationHandler::saveGraphic);
        }
        this.result = null;
        return result;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, GenericParameter parameter) {
        return null;
    }

    static public class GenericParameter<T> extends AssetLoaderParameters<HashMap<Integer, T>> {
        Json.Serializer serializer;
        Class clazz;
        Function<T, Integer> function;

        private GenericParameter(Json.Serializer serializer, Class clazz, Function<T, Integer> function) {
            this.serializer = serializer;
            this.clazz = clazz;
            this.function = function;
        }

        public static GenericParameter<BodyDescriptor> bodiesGenericParameter() {
            return new GenericParameter<>(new BodyDescriptorSerializer(), BodyDescriptor.class, BodyDescriptor::getId);
        }

        Class getClazz() {
            return clazz;
        }

        Json.Serializer getSerializer() {
            return serializer;
        }

        Function<T, Integer> getFunction() {
            return function;
        }
    }
}
