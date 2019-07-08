package game.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import game.AOGame;
import model.textures.GameTexture;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import shared.objects.types.Obj;
import shared.objects.types.Type;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ObjectHandler extends PassiveSystem {

    private Map<Integer, Obj> objects = new HashMap<>();
    private Map<Obj, GameTexture> textures = new HashMap<>();
    private Map<Obj, GameTexture> flipped = new HashMap<>();
    private AOAssetManager assetManager;

    @Override
    protected void initialize() {
        super.initialize();
        AOGame game = (AOGame) Gdx.app.getApplicationListener();
        assetManager = game.getAssetManager();
        objects = assetManager.getObjs();
    }

    public Optional<Obj> getObject(int id) {
        return Optional.ofNullable(objects.get(id));
    }

    public TextureRegion getGraphic(Obj obj) {
        return textures.computeIfAbsent(obj, presentObj -> new GameTexture(presentObj.getGrhIndex(), false)).getGraphic();
    }

    public TextureRegion getIngameGraphic(Obj obj) {
        return flipped.computeIfAbsent(obj, presentObj -> new GameTexture(presentObj.getGrhIndex(), true)).getGraphic();
    }

    public Set<Obj> getTypeObjects(Type type) {
        return objects.values().stream().filter(obj -> obj.getType().equals(type)).collect(Collectors.toSet());
    }

}
