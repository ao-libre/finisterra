package design.designers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import game.AssetManagerHolder;
import game.handlers.AOAssetManager;
import game.loaders.DescriptorsLoader;
import model.descriptors.*;
import org.jetbrains.annotations.NotNull;
import shared.util.AOJson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


public class DescriptorDesigner<T extends Descriptor> implements IDesigner<T, IDesigner.Parameters<T>> {

    private final String JSON_EXT = ".json";
    private final String OUTPUT_FOLDER = "output/";

    private AOJson json = new AOJson();
    private List<T> descriptors;
    private Class<T> tClass;

    public DescriptorDesigner(Class<T> tClass) {
        this.tClass = tClass;
        load(new DescriptorParameters());
    }

    @Override
    public void load(Parameters<T> params) {
        AssetManagerHolder game = (AssetManagerHolder) Gdx.app.getApplicationListener();
        AOAssetManager assetManager = game.getAssetManager();
        if (tClass.equals(HeadDescriptor.class)) {
            descriptors = assetManager.getHeads().stream().map(tClass::cast).collect(Collectors.toList());
        } else if (tClass.equals(BodyDescriptor.class)) {
            Map<Integer, BodyDescriptor> bodies = assetManager.getBodies();
            descriptors = bodies.values().stream().map(tClass::cast).collect(Collectors.toList());
        } else if (tClass.equals(FXDescriptor.class)) {
            descriptors = assetManager.getFXs().stream().map(tClass::cast).collect(Collectors.toList());
        } else if (tClass.equals(HelmetDescriptor.class)) {
            descriptors = assetManager.getHelmets().stream().map(tClass::cast).collect(Collectors.toList());
        } else if (tClass.equals(ShieldDescriptor.class)) {
            descriptors = assetManager.getShields().stream().map(tClass::cast).collect(Collectors.toList());
        } else if (tClass.equals(WeaponDescriptor.class)) {
            descriptors = assetManager.getWeapons().stream().map(tClass::cast).collect(Collectors.toList());
        }
        for (int i = 0; i < descriptors.size(); i++) {
            descriptors.get(i).setId(i + 1);
        }
    }

    @Override
    public void reload() {

    }

    @Override
    public void save() {
        json.toJson(descriptors, ArrayList.class, tClass, Gdx.files.local(OUTPUT_FOLDER + getFileName() + JSON_EXT));
    }

    @NotNull
    private String getFileName() {
        String fileName = "file";
        if (tClass.equals(HeadDescriptor.class)) {
            fileName = DescriptorsLoader.HEADS;
        } else if (tClass.equals(BodyDescriptor.class)) {
            fileName = DescriptorsLoader.BODIES;
        } else if (tClass.equals(FXDescriptor.class)) {
            fileName = DescriptorsLoader.FXS;
        } else if (tClass.equals(HelmetDescriptor.class)) {
            fileName = DescriptorsLoader.HELMETS;
        } else if (tClass.equals(ShieldDescriptor.class)) {
            fileName = DescriptorsLoader.SHIELDS;
        } else if (tClass.equals(WeaponDescriptor.class)) {
            fileName = DescriptorsLoader.WEAPONS;
        }
        return fileName;
    }

    @Override
    public List<T> get() {
        return descriptors;
    }

    @Override
    public Optional<T> get(int id) {
        return descriptors.stream().filter(a -> id == a.getId()).findFirst();
    }

    @Override
    public T create() {
        T t = null;
        if (tClass.equals(HeadDescriptor.class)) {
            t = (T) new HeadDescriptor();
        } else if (tClass.equals(BodyDescriptor.class)) {
            t = (T) new BodyDescriptor();
        } else if (tClass.equals(FXDescriptor.class)) {
            t = (T) new FXDescriptor();
        } else if (tClass.equals(HelmetDescriptor.class)) {
            t = (T) new HelmetDescriptor();
        } else if (tClass.equals(ShieldDescriptor.class)) {
            t = (T) new ShieldDescriptor();
        } else if (tClass.equals(WeaponDescriptor.class)) {
            t = (T) new WeaponDescriptor();
        }
        return t;
    }

    @Override
    public void modify(Descriptor element, Stage stage) {

    }

    @Override
    public void delete(Descriptor element) {

    }

    class DescriptorParameters implements Parameters<T> {
    }

}
