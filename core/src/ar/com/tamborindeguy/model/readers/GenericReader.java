package ar.com.tamborindeguy.model.readers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GenericReader<T> {
    public Map<Integer, T> read(String fileName, Class type, Json.Serializer serializer, Function<T, Integer> function) {
        FileHandle file = Gdx.files.internal("data/indices/" + fileName + ".json");
        Json json = new Json();
        json.setSerializer(type, serializer);
        ArrayList<T> list = json.fromJson(ArrayList.class, type, file);
        return (Map) (list.stream().filter(item -> function.apply(item) != 0).collect(Collectors.toMap(function, Function.identity())));
    }
}
