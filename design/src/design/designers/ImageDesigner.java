package design.designers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import game.AssetManagerHolder;
import game.handlers.AOAssetManager;
import model.textures.AOImage;
import shared.util.AOJson;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static design.designers.ImageDesigner.ImageParameters;

public class ImageDesigner implements IDesigner<AOImage, ImageParameters> {

    private final String IMAGES_FILE_NAME = "images";
    private final String JSON_EXT = ".json";
    private final String OUTPUT_FOLDER = "output/";

    private AOJson json = new AOJson();
    private List<AOImage> images;

    private int getFreeId() {
        return 0;
    }

    public ImageDesigner(ImageParameters parameters) {
        load(parameters);
    }

    @Override
    public void load(ImageParameters params) {
        AssetManagerHolder game = (AssetManagerHolder) Gdx.app.getApplicationListener();
        AOAssetManager assetManager = game.getAssetManager();
        images = assetManager.getImages();
    }

    @Override
    public void reload() {

    }

    @Override
    public void save() {
        json.toJson(images, ArrayList.class, AOImage.class, Gdx.files.local(OUTPUT_FOLDER + IMAGES_FILE_NAME + JSON_EXT));
    }

    @Override
    public List<AOImage> get() {
        return images;
    }

    @Override
    public Optional<AOImage> get(int id) {
        return images.stream().filter(a -> id == a.getId()).findFirst();
    }

    @Override
    public AOImage create() {
        return null;
    }

    @Override
    public void modify(AOImage element, Stage stage) {
    }

    @Override
    public void delete(AOImage element) {
    }

    @Override
    public void add(AOImage aoImage) {
        images.set(aoImage.getId() - 1, aoImage);
    }

    public static class ImageParameters implements Parameters<AOImage> {
    }
}
