package ar.com.tamborindeguy.client.handlers;

import ar.com.tamborindeguy.model.Objects;
import ar.com.tamborindeguy.model.readers.AODescriptorsReader;
import ar.com.tamborindeguy.model.readers.DescriptorsReader;
import ar.com.tamborindeguy.model.textures.GameTexture;
import ar.com.tamborindeguy.objects.types.Obj;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ObjectHandler {

    private static DescriptorsReader reader = new AODescriptorsReader();
    private static Objects objects;
    private static Map<Obj, GameTexture> textures = new HashMap<>();

    public static void load() {
        objects = reader.loadObjects("obj");
    }

    public static Optional<Obj> getObject(int id) {
        return objects.getObject(id);
    }

    public static TextureRegion getGraphic(Obj obj) {
        return textures.computeIfAbsent(obj, presentObj -> new GameTexture(presentObj.getGrhIndex())).getGraphic();

    }
}
