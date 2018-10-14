package ar.com.tamborindeguy.client.handlers;

import ar.com.tamborindeguy.model.readers.AODescriptorsReader;
import ar.com.tamborindeguy.model.readers.DescriptorsReader;
import ar.com.tamborindeguy.model.textures.GameTexture;
import ar.com.tamborindeguy.objects.types.Obj;
import ar.com.tamborindeguy.objects.types.Type;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ObjectHandler {

    private static DescriptorsReader reader = new AODescriptorsReader();
    private static Map<Integer, Obj> objects;
    private static Map<Obj, GameTexture> textures = new HashMap<>();
    private static Map<Obj, GameTexture> flipped = new HashMap<>();

    public static void load() {
        objects = reader.loadObjects("obj");
    }

    public static Optional<Obj> getObject(int id) {
        return Optional.of(objects.get(id));
    }

    public static TextureRegion getGraphic(Obj obj) {
        return textures.computeIfAbsent(obj, presentObj -> new GameTexture(presentObj.getGrhIndex(), false)).getGraphic();
    }

    public static TextureRegion getIngameGraphic(Obj obj) {
        return flipped.computeIfAbsent(obj, presentObj -> new GameTexture(presentObj.getGrhIndex(), true)).getGraphic();
    }

    public static Set<Obj> getTypeObjects(Type type) {
        return objects.values().stream().filter(obj -> obj.getType().equals(type)).collect(Collectors.toSet());
    }

}
