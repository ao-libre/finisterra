package design.designers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import design.dialogs.TileSetCreator;
import design.screens.map.model.TileSet;
import shared.util.AOJson;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static design.designers.TileSetDesigner.TileSetParameters;
import static design.utils.FileUtils.openDialog;

public class TileSetDesigner implements IDesigner<TileSet, TileSetParameters> {

    private Map<Integer, TileSet> tileSets;
    private AOJson json = new AOJson();

    public TileSetDesigner() {
        tileSets = new HashMap<>();
        load(new TileSetParameters());
    }

    @Override
    public void load(TileSetParameters params) {
        FileHandle tilesetsFile = Gdx.files.local("design/tileset.json");
        if (tilesetsFile.exists()) {
            ArrayList<TileSet> tileSetsList = json.fromJson(ArrayList.class, TileSet.class, tilesetsFile);
            tileSetsList.forEach(tileSet -> tileSets.put(tileSet.getId(), tileSet));
        }
    }

    @Override
    public void reload() {

    }

    @Override
    public void save() {
        FileHandle outputFile = Gdx.files.local("output/tileset.json");
        List<TileSet> list = tileSets.values().stream().sorted(Comparator.comparingInt(TileSet::getId)).collect(Collectors.toList());
        json.toJson(list, ArrayList.class, TileSet.class, outputFile);
    }

    @Override
    public Map<Integer, TileSet> get() {
        return tileSets;
    }

    @Override
    public Optional<TileSet> get(int id) {
        return Optional.ofNullable(tileSets.get(id));
    }

    @Override
    public Optional<TileSet> create() {
        Optional<TileSet> result = Optional.empty();
        File file = openDialog("Choose tile set", "", new String[]{"*.png"}, "");
        if (file == null) {
            return result;
        }
        FileHandle fileHandle = new FileHandle(file);
        return Optional.of(create(fileHandle));
    }

    public TileSet create(FileHandle fileHandle) {
        TileSet tileSet = TileSetCreator.create(fileHandle, getFreeId());
        add(tileSet);
        return tileSet;
    }

    private int getFreeId() {
        return tileSets.isEmpty() ? 1 : tileSets.keySet().stream().max(Comparator.comparingInt(value -> value)).get() + 1;
    }

    @Override
    public void modify(TileSet element, Stage stage) {
    }

    @Override
    public void delete(TileSet element) {
        tileSets.remove(element.getId());
    }

    @Override
    public void add(TileSet tileSet) {
        tileSets.put(tileSet.getId(), tileSet);
    }

    @Override
    public boolean contains(int id) {
        return tileSets.containsKey(id);
    }

    @Override
    public void markUsedImages() {
    }

    static class TileSetParameters implements Parameters<TileSet> {
    }
}
