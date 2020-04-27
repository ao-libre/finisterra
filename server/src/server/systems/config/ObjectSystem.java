package server.systems.config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.esotericsoftware.minlog.Log;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import shared.objects.types.Obj;
import shared.objects.types.Type;
import shared.util.ObjJson;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Load and contains all the objects
 */
public class ObjectSystem extends PassiveSystem {

    private Map<Integer, Obj> objects = new HashMap<>();

    public ObjectSystem() {
        init();
    }

    private void init() {
        Log.info("Server initialization", "Loading objects...");
        final FileHandle folder = Gdx.files.internal("objects/");
        ObjJson.loadObjectsByType(objects, folder);
    }

    public Optional<Obj> getObject(int id) {
        return Optional.of(objects.get(id));
    }

    public Set<Obj> getTypeObjects(Type type) {
        return objects.values().stream().filter(obj -> obj.getType().equals(type)).collect(Collectors.toSet());
    }

}
