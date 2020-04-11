package design.designers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.esotericsoftware.minlog.Log;
import design.dialogs.AnimationFromImages;
import design.dialogs.SplitImage;
import design.editors.ImageEditor;
import design.screens.ScreenEnum;
import design.screens.views.ImageView;
import design.screens.views.View;
import game.AOGame;
import game.AssetManagerHolder;
import game.handlers.AOAssetManager;
import game.handlers.DefaultAOAssetManager;
import game.utils.Resources;
import model.textures.AOAnimation;
import model.textures.AOImage;
import shared.util.AOJson;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

import static design.designers.ImageDesigner.ImageParameters;
import static design.utils.FileUtils.openDialog;

public class ImageDesigner implements IDesigner<AOImage, ImageParameters> {

    private final String IMAGES_FILE_NAME = "images";
    private final String JSON_EXT = ".json";
    private final String OUTPUT_FOLDER = "output/";

    private AOJson json = new AOJson();
    private Map<Integer, AOImage> images;

    public ImageDesigner(ImageParameters parameters) {
        load(parameters);
    }

    public int getFreeId() {
        AnimationDesigner designer = (AnimationDesigner) ScreenEnum.ANIMATION_VIEW.getScreen().getDesigner();
        int freeAnimation = designer.get().values().stream().max(Comparator.comparingInt(AOAnimation::getId)).get().getId() + 1;
        int freeImage = images.values().stream().max(Comparator.comparingInt(AOImage::getId)).get().getId() + 1;
        return Math.max(freeAnimation, freeImage);
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
        images.values().forEach(AOImage::adjust);
        json.toJson(new ArrayList<>(images.values()), ArrayList.class, AOImage.class, Gdx.files.local(OUTPUT_FOLDER + IMAGES_FILE_NAME + JSON_EXT));
    }

    @Override
    public Map<Integer, AOImage> get() {
        return images;
    }

    @Override
    public Optional<AOImage> get(int id) {
        return Optional.ofNullable(images.get(id));
    }

    @Override
    public Optional<AOImage> create() {
        // open file chooser
        Optional<AOImage> result = Optional.empty();
        File file = openDialog("Search Image", "", new String[]{"*.png"}, "");
        if (file == null) {
            return result;
        }

        FileHandle fileHandle = new FileHandle(file);
        return create(fileHandle);
    }

    public Optional<AOImage> create(FileHandle fileHandle) {
        int freeId = getFreeId();
        FileHandle dest = Gdx.files.local(Resources.GAME_GRAPHICS_PATH + freeId + ".png");
        fileHandle.copyTo(dest);
        AssetManagerHolder game = (AssetManagerHolder) Gdx.app.getApplicationListener();
        AOAssetManager assetManager = game.getAssetManager();
        if (assetManager instanceof DefaultAOAssetManager) {
            DefaultAOAssetManager defaultAOAssetManager = (DefaultAOAssetManager) assetManager;
            defaultAOAssetManager.load(dest.path(), Texture.class);
            defaultAOAssetManager.finishLoadingAsset(dest.path());
        }

        var image = new Pixmap(dest);
        AOImage aoImage = new AOImage();
        aoImage.setId(freeId);
        aoImage.setHeight(image.getHeight());
        aoImage.setWidth(image.getWidth());
        aoImage.setFileNum(freeId);
        add(aoImage);
        assetManager.getImages().put(aoImage.getId(), aoImage);
        return Optional.of(aoImage);
    }

    @Override
    public void modify(AOImage element, Stage stage) {
        ImageEditor imageEditor = new ImageEditor(new AOImage(element)) {
            @Override
            protected void result(Object object) {
                if (object instanceof AOImage) {
                    // refresh view
                    View screen = ScreenEnum.IMAGE_VIEW.getScreen();
                    AOImage image = (AOImage) object;
                    ((ImageView) screen).evict(image);
                    images.put((image).getId(), image);
                    screen.loadItems(Optional.of(image));
                }
            }
        };
        imageEditor.show(stage);
    }

    public void splitImage(int imageId) {
        get(imageId).ifPresent(image -> {
            SplitImage.split(image, ScreenEnum.IMAGE_VIEW.getScreen().getStage(), (list) -> {
                delete(image);
                for (AOImage aoImage : list) {
                    aoImage.setId(getFreeId());
                    add(aoImage);
                }
                View screen = ScreenEnum.IMAGE_VIEW.getScreen();
                screen.loadItems(Optional.ofNullable(list.get(0)));
                AnimationFromImages.show(list);
            });
        });

    }

    @Override
    public void delete(AOImage element) {
        images.remove(element.getId());
    }

    @Override
    public void add(AOImage aoImage) {
        images.put(aoImage.getId(), aoImage);
        Log.info("Image id: " + aoImage.getId() + ". Added!");
    }

    @Override
    public boolean contains(int id) {
        return images.containsKey(id);
    }

    @Override
    public void markUsedImages() {

    }

    public static class ImageParameters implements Parameters<AOImage> {
    }
}
