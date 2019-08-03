package design.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import design.screens.ScreenEnum;
import design.screens.views.ImageView;
import design.screens.views.View;
import model.textures.AOImage;
import shared.util.AOJson;

import java.util.*;

import static launcher.DesignCenter.SKIN;

public class OptimizeImages extends Dialog {

    public OptimizeImages(Skin skin) {
        super("Optimize Imports", skin);
        pad(20);
        text("Unused images will be moved to a new file");
        button("OK", true);
        button("Cancel", false);
    }

    @Override
    protected void result(Object object) {
        if ((Boolean) object) {
            doOptimize();
        }
    }

    private void doOptimize() {
        ImageView imageView = (ImageView) ScreenEnum.IMAGE_VIEW.getScreen();
        imageView.clearUsedImages();
        Arrays.stream(ScreenEnum.values()).forEach(screenEnum -> {
            View screen = screenEnum.getScreen();
            if (!screen.equals(imageView)) {
                screen.getDesigner().markUsedImages();
            }
        });
        HashMap<Integer, AOImage> images = new HashMap<>(imageView.getDesigner().get());
        Set<Integer> usedImages = imageView.getUsedImages();
        usedImages.forEach(images::remove);

        Dialog ask = new Dialog("Save?", SKIN) {
            @Override
            protected void result(Object object) {
                if ((Boolean) object) {
                    images.forEach((k, v) -> imageView.getDesigner().delete(v));
                    save(images);
                }
            }
        };
        if (images.size() > 0) {
            ask.text(images.size() + " unused images found. Do you want to save in auxiliary file (output/unused-image.json)?");
        } else {
            ask.text("No unused images found");
        }
        ask.button("OK", true);
        ask.button("Cancel", false);
        ask.show(getStage());
    }

    private void save(HashMap<Integer, AOImage> allImages) {
        AOJson json = new AOJson();
        List<AOImage> values = new ArrayList<>(allImages.values());
        json.toJson(values, ArrayList.class, AOImage.class, Gdx.files.local("output/unused-images.json"));
    }
}
