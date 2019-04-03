package game.handlers;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import model.readers.AODescriptorsReader;
import model.textures.GameTexture;
import shared.model.readers.DescriptorsReader;
import shared.objects.types.Obj;
import shared.objects.types.Type;

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
