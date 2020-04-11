package game.systems.resources;

import com.artemis.annotations.Wire;
import game.handlers.DefaultAOAssetManager;
import model.descriptors.*;
import net.mostlyoriginal.api.system.core.PassiveSystem;

public class DescriptorsSystem extends PassiveSystem {

    @Wire
    private DefaultAOAssetManager assetManager;

    public DescriptorsSystem() {}

    public BodyDescriptor getBody(int index) {
        return assetManager.getBodies().get(index);
    }

    public HeadDescriptor getHead(int index) {
        return assetManager.getHeads().get(index);
    }

    public HelmetDescriptor getHelmet(int index) {
        return assetManager.getHelmets().get(index);
    }

    public FXDescriptor getFX(int index) {
        return assetManager.getFXs().get(index);
    }

    public ShieldDescriptor getShield(int index) {
        return assetManager.getShields().get(index);
    }

    public WeaponDescriptor getWeapon(int index) {
        return assetManager.getWeapons().get(index);
    }

    public boolean hasHelmet(int id) {
        return assetManager.getHelmets().containsKey(id);
    }

    public boolean hasBody(int id) {
        return assetManager.getBodies().containsKey(id);
    }

    public boolean hasHead(int id) {
        return assetManager.getHeads().containsKey(id);
    }

    public boolean hasShield(int id) {
        return assetManager.getShields().containsKey(id);
    }

    public boolean hasWeapon(int id) {
        return assetManager.getWeapons().containsKey(id);
    }
}
