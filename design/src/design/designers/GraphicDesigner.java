package design.designers;

import com.badlogic.gdx.Gdx;
import game.AssetManagerHolder;
import game.handlers.AOAssetManager;
import shared.model.Graphic;
import shared.util.AOJson;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static design.designers.GraphicDesigner.GraphicParameters;

public class GraphicDesigner implements IDesigner<Graphic, GraphicParameters> {

    private final String IMAGES_FILE_NAME = "images";
    private final String ANIMATIONS_FILE_NAME = "animations";
    private final String JSON_EXT = ".json";

    private final String OUTPUT_FOLDER = "output/";

    private AOJson json = new AOJson();
    private Map<Integer, Graphic> graphics;

    private int getFreeId() {
        return 0;
    }

    public GraphicDesigner(GraphicParameters parameters) {
        load(parameters);
    }

    @Override
    public void load(GraphicParameters params) {
        AssetManagerHolder game = (AssetManagerHolder) Gdx.app.getApplicationListener();
        AOAssetManager assetManager = game.getAssetManager();
        graphics = assetManager.getGraphics();
    }

    @Override
    public void save() {
        List<Graphic> animations = graphics.values().stream().filter(graphic -> graphic.getFrames().length > 1).collect(Collectors.toList());
        json.toJson(animations, List.class, Graphic.class, Gdx.files.local(OUTPUT_FOLDER + ANIMATIONS_FILE_NAME + JSON_EXT));

        List<Graphic> images = graphics.values().stream().filter(graphic -> graphic.getFrames().length <= 1).collect(Collectors.toList());
        json.toJson(images, List.class, Graphic.class, Gdx.files.local(OUTPUT_FOLDER + IMAGES_FILE_NAME + JSON_EXT));
    }

    @Override
    public Map<Integer, Graphic> get() {
        return graphics;
    }

    @Override
    public Optional<Graphic> get(int id) {
        return Optional.ofNullable(graphics.get(id));
    }

    @Override
    public Graphic create() {
        return null;
    }

    @Override
    public void modify(Graphic element) {

    }

    @Override
    public void delete(Graphic element) {
        graphics.remove(element);
    }

    public static class GraphicParameters implements Parameters<Graphic> {
    }
}
