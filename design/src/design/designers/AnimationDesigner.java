package design.designers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import game.AssetManagerHolder;
import game.handlers.AOAssetManager;
import model.textures.AOAnimation;
import shared.util.AOJson;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static design.designers.AnimationDesigner.AnimationParameters;

public class AnimationDesigner implements IDesigner<AOAnimation, AnimationParameters> {

    private final String ANIMATIONS_FILE_NAME = "animations";
    private final String JSON_EXT = ".json";

    private final String OUTPUT_FOLDER = "output/";

    private AOJson json = new AOJson();
    private List<AOAnimation> animations;

    private int getFreeId() {
        return 0;
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
    public void save() {
        json.toJson(animations, ArrayList.class, AOAnimation.class, Gdx.files.local(OUTPUT_FOLDER + ANIMATIONS_FILE_NAME + JSON_EXT));
    }

    @Override
    public List<AOAnimation> get() {
        return animations;
    }

    @Override
    public Optional<AOAnimation> get(int id) {
        return animations.stream().filter(a -> id == a.getId()).findFirst();
    }

    @Override
    public AOAnimation create() {
        return null;
    }

    @Override
    public void modify(AOAnimation element, Stage stage) {
    }

    @Override
    public void delete(AOAnimation element) {
    }

    public static class AnimationParameters implements Parameters<AOAnimation> {
    }
}
