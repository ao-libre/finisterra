package game.handlers;

import com.badlogic.gdx.Gdx;
import game.AOGame;
import game.AssetManagerHolder;
import model.descriptors.*;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import shared.model.Graphic;

import java.util.List;
import java.util.Map;

public class DescriptorHandler extends PassiveSystem {

    private AOAssetManager assetManager;

    @Override
    protected void initialize() {
        super.initialize();
        AssetManagerHolder game = (AssetManagerHolder) Gdx.app.getApplicationListener();
        assetManager = game.getAssetManager();
    }

    public Map<Integer, Graphic> getGraphics() {
        return assetManager.getGraphics();
    }

    public Map<Integer, BodyDescriptor> getBodies() {
        return assetManager.getBodies();
    }

    public List<FXDescriptor> getFxs() {
        return assetManager.getFXs();
    }

    public List<HeadDescriptor> getHeads() {
        return assetManager.getHeads();
    }

    public List<HelmetDescriptor> getHelmets() {
        return assetManager.getHelmets();
    }

    public List<ShieldDescriptor> getShields() {
        return assetManager.getShields();
    }

    public List<WeaponDescriptor> getWeapons() {
        return assetManager.getWeapons();
    }

    public BodyDescriptor getBody(int index) {
        return getBodies().get(index);
    }

    public HeadDescriptor getHead(int index) {
        return getHeads().get(index);
    }

    public HelmetDescriptor getHelmet(int index) {
        return getHelmets().get(index);
    }

    public FXDescriptor getFX(int index) {
        return getFxs().get(index);
    }

    public ShieldDescriptor getShield(int index) {
        return getShields().get(index);
    }

    public WeaponDescriptor getWeapon(int index) {
        return getWeapons().get(index);
    }

    public Graphic getGraphic(int index) {
        return getGraphics().get(index);
    }

}
