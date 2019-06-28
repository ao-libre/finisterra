package model.readers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static game.loaders.DescriptorsLoader.GAME_DESCRIPTORS_FOLDER;
import static game.loaders.DescriptorsLoader.JSON_EXTENSION;

public class GenericReader<T> {
    public Map<Integer, T> read(String fileName, Class type, Json.Serializer serializer, Function<T, Integer> function) {
        FileHandle file = Gdx.files.internal(GAME_DESCRIPTORS_FOLDER + fileName + JSON_EXTENSION);
        return read(file, type, serializer, function);
    }

    public Map<Integer, T> read(FileHandle file, Class type, Json.Serializer serializer, Function<T, Integer> function) {
        Json json = new Json();
        json.setSerializer(type, serializer);
        ArrayList<T> list = json.fromJson(ArrayList.class, type, file);
        return list.stream().filter(item -> function.apply(item) != 0).collect(Collectors.toMap(function, Function.identity()));
    }
}
