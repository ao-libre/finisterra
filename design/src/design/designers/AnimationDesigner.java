package design.designers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import design.editors.utils.SliceResult;
import design.editors.utils.Slicer;
import design.screens.ScreenEnum;
import design.screens.views.AnimationView;
import design.screens.views.ImageView;
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

import static design.designers.AnimationDesigner.AnimationParameters;
import static design.utils.FileUtils.openDialog;

public class AnimationDesigner implements IDesigner<AOAnimation, AnimationParameters> {

    private final String ANIMATIONS_FILE_NAME = "animations";
    private final String JSON_EXT = ".json";

    private final String OUTPUT_FOLDER = "output/";

    private AOJson json = new AOJson();
    private Map<Integer, AOAnimation> animations;

    public AnimationDesigner(AnimationParameters parameters) {
        load(parameters);
    }

    public int getFreeId() {
        ImageDesigner designer = (ImageDesigner) ScreenEnum.IMAGE_VIEW.getScreen().getDesigner();
        int freeImage = designer.get().values().stream().max(Comparator.comparingInt(AOImage::getId)).get().getId() + 1;
        int freeAnimation = animations.values().stream().max(Comparator.comparingInt(AOAnimation::getId)).get().getId() + 1;
        return Math.max(freeAnimation, freeImage);
    }

    @Override
    public void load(AnimationParameters params) {
        AssetManagerHolder game = (AssetManagerHolder) Gdx.app.getApplicationListener();
        AOAssetManager assetManager = game.getAssetManager();
        animations = assetManager.getAnimations();
    }

    @Override
    public void reload() {

    }

    @Override
    public void save() {
        ArrayList<AOAnimation> list = new ArrayList<>(animations.values());
        list.sort(Comparator.comparingInt(AOAnimation::getId));
        json.toJson(list, ArrayList.class, AOAnimation.class, Gdx.files.local(OUTPUT_FOLDER + ANIMATIONS_FILE_NAME + JSON_EXT));
    }

    @Override
    public Map<Integer, AOAnimation> get() {
        return animations;
    }

    @Override
    public Optional<AOAnimation> get(int id) {
        return Optional.ofNullable(animations.get(id));
    }

    @Override
    public Optional<AOAnimation> create() {
        AOAnimation animation = new AOAnimation();
        animation.setId(getFreeId());
        animations.put(animation.getId(), animation);
        return Optional.of(animation);
    }

    public void createFromFile() {
        // open file chooser
        File file = openDialog("Search Image", "", new String[]{"*.png"}, "");
        if (file == null) {
            return;
        }

        FileHandle fileHandle = new FileHandle(file);
        create(fileHandle);
    }

    public void create(FileHandle fileHandle) {
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
            ScreenEnum.IMAGE_VIEW.getScreen().getDesigner().add(image);
            assetManager.getImages().put(image.getId(), image);
        });

        if (slice.getImages().size() > 1) {
            AnimationView animationView = (AnimationView) ScreenEnum.ANIMATION_VIEW.getScreen();
            animationView.createAnimation(slice.getImages());
        }
    }

    @Override
    public void modify(AOAnimation element, Stage stage) {
    }

    @Override
    public void delete(AOAnimation element) {
        animations.remove(element.getId());
    }

    @Override
    public void add(AOAnimation animation) {
        animations.put(animation.getId(), animation);
    }

    @Override
    public boolean contains(int id) {
        return animations.containsKey(id);
    }

    @Override
    public void markUsedImages() {
        ImageView view = (ImageView) ScreenEnum.IMAGE_VIEW.getScreen();
        animations.values().forEach(animation -> {
            int[] frames = animation.getFrames();
            for (int frame : frames) {
                if (frame > 0) {
                    view.imageUsed(frame);
                }
            }
        });
    }

    public static class AnimationParameters implements Parameters<AOAnimation> {
    }
}
