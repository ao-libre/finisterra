package game.systems.resources;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import game.handlers.DefaultAOAssetManager;
import model.textures.AOImage;
import model.textures.AOTexture;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import shared.objects.types.Obj;
import shared.objects.types.Type;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Wire
public class ObjectSystem extends PassiveSystem {

    private final Map<Obj, AOTexture> textures = new HashMap<>();
    private final Map<Obj, AOTexture> flipped = new HashMap<>();
    private Map<Integer, Obj> objects = new HashMap<>();
    @Wire
    private DefaultAOAssetManager assetManager;

    @Override
    protected void initialize() {
        super.initialize();
        objects = assetManager.getObjs();
    }

    public Optional<Obj> getObject(int id) {
        return Optional.ofNullable(objects.get(id));
    }

    public TextureRegion getGraphic(Obj obj) {

        return textures.computeIfAbsent(obj, presentObj -> {
            int grhIndex = presentObj.getGrhIndex();
            AOImage aoImage = getAOImage(grhIndex);
            return new AOTexture(aoImage, assetManager.getTexture(aoImage.getFileNum()), false);
        }).getTexture();

    }

    private AOImage getAOImage(int grhIndex) {
        return assetManager.getImage(grhIndex);
    }

    public TextureRegion getIngameGraphic(Obj obj) {
        return flipped.computeIfAbsent(obj, presentObj -> {
            AOImage aoImage = getAOImage(presentObj.getGrhIndex());
            return new AOTexture(aoImage, assetManager.getTexture(aoImage.getFileNum()), true);
        }).getTexture();
    }

    public Set<Obj> getTypeObjects(Type type) {
        return objects.values().stream().filter(obj -> obj.getType().equals(type)).collect(Collectors.toSet());
    }

}
