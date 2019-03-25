package server.manager;

import server.database.ServerDescriptorReader;
import shared.model.readers.DescriptorsReader;
import shared.objects.types.Obj;
import shared.objects.types.Type;
import com.esotericsoftware.minlog.Log;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Load and contains all the objects
 */
public class ObjectManager {
    private static DescriptorsReader reader = new ServerDescriptorReader();
    private static Map<Integer, Obj> objects;

    public static void load() {
        Log.info("Loading objects...");
        objects = reader.loadObjects("obj");
    }

    public static Optional<Obj> getObject(int id) {
        return Optional.of(objects.get(id));
    }

    public static Set<Obj> getTypeObjects(Type type) {
        return objects.values().stream().filter(obj -> obj.getType().equals(type)).collect(Collectors.toSet());
    }
}
