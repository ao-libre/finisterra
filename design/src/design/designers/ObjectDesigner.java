package design.designers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import game.AssetManagerHolder;
import game.handlers.AOAssetManager;
import shared.objects.types.Obj;
import shared.util.ObjJson;

import java.util.Map;
import java.util.Optional;

import static design.designers.ObjectDesigner.ObjectParameters;

public class ObjectDesigner implements IDesigner<Obj, ObjectParameters> {

    private final String OBJECT_FOLDER_PATH = "objects/";
    private final String JSON_EXT = ".json";

    private final String OUTPUT_FOLDER = "output/";

    private Map<Integer, Obj> objs;

    public ObjectDesigner() {
        load(new ObjectParameters());
    }

    private int getFreeId() {
        return objs.keySet().stream().max(Integer::compareTo).get() + 1;
    }

    @Override
    public void load(ObjectParameters params) {
        AssetManagerHolder game = (AssetManagerHolder) Gdx.app.getApplicationListener();
        AOAssetManager assetManager = game.getAssetManager();
        objs = assetManager.getObjs();
    }

    @Override
    public void reload() {

    }

    @Override
    public void save() {
        FileHandle outputFile = Gdx.files.local(OUTPUT_FOLDER + OBJECT_FOLDER_PATH);
        ObjJson.saveObjectsByType(objs, outputFile);
    }

    @Override
    public Map<Integer, Obj> get() {
        return objs;
    }

    @Override
    public Optional<Obj> get(int id) {
        return Optional.ofNullable(objs.get(id));
    }

    @Override
    public Optional<Obj> create() {
        return Optional.empty();
    }

    @Override
    public void modify(Obj element, Stage stage) {

    }

    @Override
    public void delete(Obj element) {
        objs.remove(element.getId());
    }

    @Override
    public void add(Obj obj) {
        objs.put(obj.getId(), obj);
    }

    @Override
    public boolean contains(int id) {
        return objs.containsKey(id);
    }

    @Override
    public void markUsedImages() {

    }

    public static class ObjectParameters implements Parameters<Obj> {
    }
}
