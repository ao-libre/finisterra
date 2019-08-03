package design.designers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.esotericsoftware.minlog.Log;
import design.editors.ImageEditor;
import design.editors.utils.SliceResult;
import design.editors.utils.Slicer;
import design.editors.utils.Utils;
import design.screens.ScreenEnum;
import design.screens.ScreenManager;
import design.screens.views.AnimationView;
import design.screens.views.ImageView;
import design.screens.views.View;
import game.AssetManagerHolder;
import game.handlers.AOAssetManager;
import game.handlers.DefaultAOAssetManager;
import game.utils.Resources;
import model.textures.AOImage;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import shared.util.AOJson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static design.designers.ImageDesigner.ImageParameters;
import static org.lwjgl.system.MemoryStack.stackPush;

public class ImageDesigner implements IDesigner<AOImage, ImageParameters> {

    private final String IMAGES_FILE_NAME = "images";
    private final String JSON_EXT = ".json";
    private final String OUTPUT_FOLDER = "output/";

    private AOJson json = new AOJson();
    private Map<Integer, AOImage> images;

    private int getFreeId() {
        return images.keySet().stream().max(Integer::compareTo).get() + 1;
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
        images.values().forEach(AOImage::adjust);
        json.toJson(new ArrayList<>(images.values()), ArrayList.class, AOImage.class, Gdx.files.local(OUTPUT_FOLDER + IMAGES_FILE_NAME + JSON_EXT));
    }

    @Override
    public List<AOImage> get() {
        return new ArrayList<>(images.values());
    }

    @Override
    public Optional<AOImage> get(int id) {
        return Optional.ofNullable(images.get(id));
    }

    @Override
    public Optional<AOImage> create() {
        // open file chooser
        Optional<AOImage> result = Optional.empty();
        File file = openDialog("Search Sprite Sheet", "", new String[]{"*.png"}, "");
        if (file == null) {
            return result;
        }

        FileHandle fileHandle = new FileHandle(file);
        FileHandle dest = Gdx.files.local(Resources.GAME_GRAPHICS_PATH + getFreeId() + ".png");
        fileHandle.copyTo(dest);
        AssetManagerHolder game = (AssetManagerHolder) Gdx.app.getApplicationListener();
        AOAssetManager assetManager = game.getAssetManager();
        if (assetManager instanceof DefaultAOAssetManager) {
            DefaultAOAssetManager defaultAOAssetManager = (DefaultAOAssetManager) assetManager;
            defaultAOAssetManager.load(dest.path(), Texture.class);
            defaultAOAssetManager.finishLoadingAsset(dest.path());
        }

        SliceResult slice = new Slicer(dest).slice(getFreeId());
        slice.getImages().forEach(image -> {
            add(image);
            assetManager.getImages().put(image.getId(), image);
        });
        if (slice.getImages().size() > 1) {
            AnimationView animationView = (AnimationView) ScreenEnum.ANIMATION_VIEW.getScreen();
            animationView.createAnimation(slice.getImages());
        }
        return Optional.ofNullable(slice.getImages().get(0));
    }

    private File openDialog(String title, String defaultPath,
                            String[] filterPatterns, String filterDescription) {
        String result;

        //fix file path characters
        if (Utils.isWindows()) {
            defaultPath = defaultPath.replace("/", "\\");
        } else {
            defaultPath = defaultPath.replace("\\", "/");
        }

        if (filterPatterns != null && filterPatterns.length > 0) {
            try (MemoryStack stack = stackPush()) {
                PointerBuffer pointerBuffer = stack.mallocPointer(filterPatterns.length);

                for (String filterPattern : filterPatterns) {
                    pointerBuffer.put(stack.UTF8(filterPattern));
                }
                pointerBuffer.flip();
                result = TinyFileDialogs.tinyfd_openFileDialog(title, defaultPath, pointerBuffer, filterDescription, false);
            }
        } else {
            result = TinyFileDialogs.tinyfd_openFileDialog(title, defaultPath, null, filterDescription, false);
        }

        if (result != null) {
            return new File(result);
        } else {
            return null;
        }
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

    public static class ImageParameters implements Parameters<AOImage> {
    }
}
