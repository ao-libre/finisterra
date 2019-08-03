package design.designers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import design.screens.ScreenEnum;
import design.screens.views.ImageView;
import game.AssetManagerHolder;
import game.handlers.AOAssetManager;
import model.textures.AOAnimation;
import shared.util.AOJson;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

import static design.designers.AnimationDesigner.AnimationParameters;

public class AnimationDesigner implements IDesigner<AOAnimation, AnimationParameters> {

    private final String ANIMATIONS_FILE_NAME = "animations";
    private final String JSON_EXT = ".json";

    private final String OUTPUT_FOLDER = "output/";

    private AOJson json = new AOJson();
    private Map<Integer, AOAnimation> animations;

    public int getFreeId() {
        return animations.values().stream().max(Comparator.comparingInt(AOAnimation::getId)).get().getId() + 1;
    }

    public AnimationDesigner(AnimationParameters parameters) {
        load(parameters);
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
            for (int i = 0; i < frames.length; i++) {
                if (frames[i] > 0) {
                    view.imageUsed(frames[i]);
                }
            }
        });
    }

    public static class AnimationParameters implements Parameters<AOAnimation> {
    }
}
