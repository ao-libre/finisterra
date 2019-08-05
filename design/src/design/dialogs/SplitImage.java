package design.dialogs;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import design.editors.fields.IntegerEditor;
import model.textures.AOImage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static launcher.DesignCenter.SKIN;

public abstract class SplitImage extends Dialog {

    private int x = 1;
    private int y = 1;
    private AOImage image;

    public SplitImage(AOImage image) {
        super("Split Image", SKIN);
        this.image = image;
        add(getFieldX());
        add(getFieldY());
        button("Cancel", false);
        button("OK", true);
    }

    public static void split(AOImage image, Stage stage, Consumer<List<AOImage>> result) {
        SplitImage splitImage = new SplitImage(image) {

            @Override
            protected void subImages(List<AOImage> images) {
                result.accept(images);
            }
        };
        splitImage.show(stage);
    }

    private Actor getFieldX() {
        return IntegerEditor.create("Columns", i -> x = i, () -> x, () -> {
        });
    }

    private Actor getFieldY() {
        return IntegerEditor.create("Rows", i -> y = i, () -> y, () -> {
        });
    }

    @Override
    protected void result(Object object) {
        if ((Boolean) object) {
            doSplit();
        }
    }

    private void doSplit() {
        int width = image.getWidth();
        int height = image.getHeight();
        int swidth = width / x;
        int sheight = height / y;

        List<AOImage> images = new ArrayList<>();
        for (int i = 0; i < y; i++) {
            for (int j = 0; j < x; j++) {
                AOImage newImage = new AOImage();
                newImage.setX(j * swidth);
                newImage.setY(i * sheight);
                newImage.setWidth(swidth);
                newImage.setHeight(sheight);
                newImage.setFileNum(image.getFileNum());
                images.add(newImage);
            }
        }

        subImages(images);
    }

    protected abstract void subImages(List<AOImage> images);

}
