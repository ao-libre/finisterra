package game.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import model.textures.GameTexture;
import shared.objects.types.Obj;
import shared.objects.types.Type;
import shared.util.ObjJson;
import shared.util.SharedResources;

import java.util.*;
import java.util.stream.Collectors;

public class ObjectHandler {

    private static Map<Integer, Obj> objects = new HashMap<>();
    private static Map<Obj, GameTexture> textures = new HashMap<>();
    private static Map<Obj, GameTexture> flipped = new HashMap<>();

    public static void load() {
        ObjJson.loadObjectsByType(objects, Gdx.files.internal(SharedResources.OBJECTS_FOLDER));
    }

    public static Optional<Obj> getObject(int id) {
        return Optional.ofNullable(objects.get(id));
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
