package json;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import shared.objects.types.Obj;
import shared.objects.types.Type;
import shared.util.ObjJson;
import shared.util.SharedResources;

import java.util.*;
import java.util.stream.Collectors;

public class SeparateObjByType {

    public static void run2(String output) {
        Map<Integer, Obj> obj = ObjJson.loadFromDat("obj");
        ObjJson.saveObjectsByType(obj, Gdx.files.local(SharedResources.OBJECTS_FOLDER + output));
    }

    public static void run(String output) {
        // load
        Map<Integer, Obj> objects = new HashMap<>();
        final FileHandle folder = Gdx.files.internal(SharedResources.OBJECTS_FOLDER);
        ObjJson.loadObjectsByType(objects, folder);
        // write
        Json json = new ObjJson();
        Arrays.stream(Type.values())
                .forEach(type -> {
                    final Set<Obj> typeObjects = getTypeObjects(objects, type);
                    if (typeObjects.isEmpty()) {
                        return;
                    }
                    final Class classForType = ObjJson.getClassForType(type);
                    write(classForType, typeObjects, type, json, output);
                });
    }

    public static Set<Obj> getTypeObjects(Map<Integer, Obj> objects, Type type) {
        return objects.values().stream().filter(obj -> obj.getType().equals(type)).collect(Collectors.toSet());
    }

    public static <T extends Obj> void write(Class<T> clazz, Set<Obj> objs, Type type, Json json, String output) {
        List<T> list = new ArrayList<>();
        if (clazz != null && objs != null && !objs.isEmpty()) {
            objs
                    .stream()
                    .filter(obj -> clazz.isInstance(obj))
                    .map(obj -> clazz.cast(obj))
                    .forEach(obj -> list.add(obj));
            list.sort(Comparator.comparingInt(Obj::getId));
        }
        final FileHandle file = Gdx.files.local(output + "/" + type.name().toLowerCase() + ".json");
        json.toJson(list, clazz, clazz, file);
    }

}
