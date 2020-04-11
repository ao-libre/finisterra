package design.designers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import design.screens.ScreenEnum;
import design.screens.views.ImageView;
import game.AOGame;
import game.AssetManagerHolder;
import game.handlers.AOAssetManager;
import game.loaders.DescriptorsLoader;
import model.descriptors.*;
import org.jetbrains.annotations.NotNull;
import shared.util.AOJson;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class DescriptorDesigner<T extends Descriptor> implements IDesigner<T, IDesigner.Parameters<T>> {

    private final String JSON_EXT = ".json";
    private final String OUTPUT_FOLDER = "output/";

    private AOJson json = new AOJson();
    private Map<Integer, T> descriptors;
    private Class<T> tClass;

    public DescriptorDesigner(Class<T> tClass) {
        this.tClass = tClass;
        load(new DescriptorParameters());
    }

    public Class<T> gettClass() {
        return tClass;
    }

    @Override
    public void load(Parameters<T> params) {
        AssetManagerHolder game = (AssetManagerHolder) Gdx.app.getApplicationListener();
        AOAssetManager assetManager = game.getAssetManager();

        Collection<? extends Descriptor> values = Collections.emptyList();

        if (tClass.equals(HeadDescriptor.class)) {
            values = assetManager.getHeads().values();
        } else if (tClass.equals(BodyDescriptor.class)) {
            values = assetManager.getBodies().values();
        } else if (tClass.equals(FXDescriptor.class)) {
            values = assetManager.getFXs().values();
        } else if (tClass.equals(HelmetDescriptor.class)) {
            values = assetManager.getHelmets().values();
        } else if (tClass.equals(ShieldDescriptor.class)) {
            values = assetManager.getShields().values();
        } else if (tClass.equals(WeaponDescriptor.class)) {
            values = assetManager.getWeapons().values();
        }
        descriptors = toMap(values);
    }

    @NotNull
    private Map<Integer, T> toMap(Collection<? extends Descriptor> values) {
        return values.stream().map(tClass::cast).collect(Collectors.toMap(Descriptor::getId, o -> o));
    }

    @Override
    public void reload() {
        load(new DescriptorParameters());
    }

    @Override
    public void save() {
        List<Descriptor> toSave = descriptors.values()
                .stream()
                .filter(this::anyAnimation)
                .sorted(Comparator.comparingInt(Descriptor::getId))
                .collect(Collectors.toList());
        json.toJson(toSave, ArrayList.class, tClass, Gdx.files.local(OUTPUT_FOLDER + getFileName() + JSON_EXT));
    }

    private boolean anyAnimation(T t) {
        return Stream.of(t.getIndexs()).flatMapToInt(Arrays::stream).anyMatch(i -> i > 0);
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
    public Map<Integer, T> get() {
        return descriptors;
    }

    @Override
    public Optional<T> get(int id) {
        return Optional.ofNullable(descriptors.get(id));
    }

    @Override
    public Optional<T> create() {
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
        t.setId(getFreeId());
        descriptors.put(t.getId(), t);
        return Optional.ofNullable(t);
    }

    private int getFreeId() {
        return descriptors.values().stream().max(Comparator.comparingInt(Descriptor::getId)).get().getId() + 1;
    }

    @Override
    public void add(T t) {
        descriptors.put(t.getId(), t);
    }

    @Override
    public boolean contains(int id) {
        return descriptors.containsKey(id);
    }

    @Override
    public void markUsedImages() {
        if (tClass.equals(HelmetDescriptor.class) ||
                tClass.equals(HeadDescriptor.class)) {
            ImageView view = (ImageView) ScreenEnum.IMAGE_VIEW.getScreen();
            descriptors.values().forEach(descriptor -> {
                int[] indexs = descriptor.getIndexs();
                for (int i = 0; i < indexs.length; i++) {
                    if (indexs[i] > 0) {
                        view.imageUsed(indexs[i]);
                    }
                }
            });
        }

    }

    @Override
    public void modify(Descriptor element, Stage stage) {
    }

    @Override
    public void delete(Descriptor element) {
        descriptors.remove(element.id);
    }

    private class DescriptorParameters implements Parameters<T> {
    }

}
