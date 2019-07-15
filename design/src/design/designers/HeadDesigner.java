package design.designers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import game.AssetManagerHolder;
import game.handlers.AOAssetManager;
import model.descriptors.HeadDescriptor;
import model.textures.AOAnimation;
import shared.util.AOJson;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static design.designers.HeadDesigner.*;

public class HeadDesigner implements IDesigner<HeadDescriptor, HeadParameters> {

    private final String HEADS_FILE_NAME = "heads";
    private final String JSON_EXT = ".json";

    private final String OUTPUT_FOLDER = "output/";

    private AOJson json = new AOJson();
    private List<HeadDescriptor> heads;

    private int getFreeId() {
        return 0;
    }

    public HeadDesigner(HeadParameters parameters) {
        load(parameters);
    }

    @Override
    public void load(HeadParameters params) {
        AssetManagerHolder game = (AssetManagerHolder) Gdx.app.getApplicationListener();
        AOAssetManager assetManager = game.getAssetManager();
        heads = assetManager.getHeads();
    }

    @Override
    public void save() {
        json.toJson(heads, ArrayList.class, AOAnimation.class, Gdx.files.local(OUTPUT_FOLDER + HEADS_FILE_NAME + JSON_EXT));
    }

    @Override
    public List<HeadDescriptor> get() {
        return heads;
    }

    @Override
    public Optional<HeadDescriptor> get(int id) {
        return heads.stream().filter(a -> id == a.getId()).findFirst();
    }

    @Override
    public HeadDescriptor create() {
        return null;
    }

    @Override
    public void modify(HeadDescriptor element, Stage stage) {
    }

    @Override
    public void delete(HeadDescriptor element) {
    }

    public static class HeadParameters implements Parameters<HeadDescriptor> {
    }
}
