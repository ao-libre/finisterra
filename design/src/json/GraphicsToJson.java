package json;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import shared.model.Spell;
import shared.objects.types.Obj;
import shared.objects.types.Type;
import shared.util.SharedResources;
import shared.util.SpellJson;

import java.util.*;
import java.util.stream.Collectors;

public class GraphicsToJson {

    public static void run(String output) {
        // load
//        SpellHandler.load();
        HashMap<Integer, Spell> spells = new HashMap<>();
        final FileHandle file = Gdx.files.local(output + SharedResources.GRAPHICS_FOLDER + SharedResources.JSON_EXT);
        SpellJson.load(spells, file);
        // write
        Json json = new SpellJson();
        json.toJson(spells, HashMap.class, Spell.class, file);
    }

    public static Set<Obj> getTypeObjects(Map<Integer, Obj> objects, Type type) {
        return objects.values().stream().filter(obj -> obj.getType().equals(type)).collect(Collectors.toSet());
    }

    public static <T extends Obj> void write(Class<T> clazz, Collection<Spell> spells, Type type, Json json, String output) {
        List<T> list = new ArrayList<>();
        if (clazz != null && spells != null && !spells.isEmpty()) {
            spells
                    .stream()
                    .filter(clazz::isInstance)
                    .map(clazz::cast)
                    .forEach(list::add);
            list.sort(Comparator.comparingInt(Obj::getId));
        }

    }

}
