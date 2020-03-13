package design.screens.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import design.designers.ImageDesigner;
import design.designers.ImageDesigner.ImageParameters;
import design.dialogs.OptimizeImages;
import design.editors.fields.Listener;
import game.screens.WorldScreen;
import model.textures.AOImage;
import model.textures.AOTexture;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import static launcher.DesignCenter.SKIN;

public class ImageView extends View<AOImage, ImageDesigner> implements WorldScreen {

    private final static int[] sizes = {64, 128, 150, 200, 250};
    private Map<AOImage, Drawable> drawables;
    private Map<Integer, Integer> usedImages = new HashMap<>();
    private Table content;
    private Button selected;

    public ImageView() {
        super(new ImageDesigner(new ImageParameters()));
    }

    public ImageView(ImageParameters parameters) {
        super(new ImageDesigner(parameters));
    }

    public void imageUsed(int imageId) {
        Integer count = 1;
        if (usedImages.containsKey(imageId)) {
            count = usedImages.get(imageId) + 1;
        }
        usedImages.put(imageId, count);
    }

    public Set<Integer> getUsedImages() {
        return usedImages.keySet();
    }

    private Drawable getTextureDrawable(AOImage image) {
        AOTexture texture = getAnimationHandler().getTexture(image.getId());
        TextureRegion texture1 = texture.getTexture();
        if (texture1.isFlipY()) {
            texture1.flip(false, true);
        }
        return new TextureRegionDrawable(texture1);
    }

    public void evict(AOImage image) {
        getAnimationHandler().clearImage(image);
    }

    @Override
    public void loadItems(Optional<AOImage> selection) {
        float x = 0, y = 0;
        if (selected != null) {
            x = selected.getX();
            y = selected.getY();
        }
        Cell<Table> cell = getMainTable().getCell(content);
        content.clear();
        createContent();
        cell.setActor(content);
        ScrollPane pane = (ScrollPane) content.getChild(1);
        pane.scrollTo(x, y, 100, 100);
        selection.ifPresent(this::scrollTo);
    }

    private void scrollTo(AOImage aoImage) {
        // TODO
        Actor child = content.getChild(1);
        if (child instanceof ScrollPane) {
            ScrollPane scrollPane = (ScrollPane) child;
            int size = sizes[1];
            int columns = ((Gdx.graphics.getWidth() - size) / size) - 1;

        }
    }

    @Override
    protected Table createContent() {
        content = new Table();
        Table buttons = new Table();
        content.add(buttons).growX().row();
        addButtons(buttons);
        Collection<AOImage> aoImages = getDesigner().get().values();
        drawables = aoImages.stream().collect(Collectors.toMap(image -> image, this::getTextureDrawable));
        Table scrollableContent = new Table();
        scrollableContent.pad(5);
        scrollableContent.defaults().space(5);
        int size = sizes[1];
        int columns = ((Gdx.graphics.getWidth() - size) / size) - 1;
        final int[] i = new int[1];
        aoImages.forEach(aoImage -> {
            scrollableContent.add(getImageContainer(aoImage)).size(size, size).fill();
            i[0]++;
            if (i[0] % columns == 0) {
                scrollableContent.row();
            }
        });
        ScrollPane scrollPa = new ScrollPane(scrollableContent, SKIN);
        scrollPa.setFlickScroll(false);
        scrollPa.setFadeScrollBars(false);
        scrollPa.setScrollbarsVisible(true);
        content.add(scrollPa);
        return content;
    }

    @Override
    protected void addButtons(Table buttons) {
        Button create = new Button(SKIN, "new");
        create.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                loadItems(getDesigner().create());
            }
        });
        Button save = new TextButton("Save", SKIN, "file");
        save.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getDesigner().save();
            }
        });
        buttons.add(create).left();
        buttons.add(save).left().expandX();
        Button optimize = new TextButton("Optimize images", SKIN, "file");
        optimize.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                OptimizeImages optimizeImages = new OptimizeImages(SKIN);
                optimizeImages.show(getStage());
            }
        });
        buttons.add(optimize).right();
    }

    @NotNull
    private Table getImageContainer(AOImage aoImage) {
        Button table = new Button(SKIN, "color-base");
        table.addListener(new ClickListener() {

            private void accept(Listener listener) {
                listener.select(aoImage);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (getTapCount() >= 2) {
                    getListenerList().forEach(this::accept);
                }
            }
        });
        Table buttons = new Table();
        Button edit = new Button(SKIN, "settings-small");
        edit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getDesigner().modify(aoImage, getStage());
            }
        });
        TextTooltip toolTip = new TextTooltip("Edit Image", SKIN);
        edit.addListener(toolTip);
        buttons.add(edit).right().expandX();

        Button split = new ImageButton(SKIN, "grid-light");
        split.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                evict(aoImage);
                getDesigner().splitImage(aoImage.getId());
            }
        });
        split.addListener(new TextTooltip("Split image in columns and rows", SKIN));
        buttons.add(split).right();

        Button duplicate = new Button(SKIN, "duplicate");
        duplicate.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                AOImage copy = new AOImage(aoImage);
                copy.setId(getDesigner().getFreeId());
                getDesigner().add(copy);
                loadItems(Optional.empty());
            }
        });
        buttons.add(duplicate).right();

        Button delete = new Button(SKIN, "delete-small");
        delete.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                evict(aoImage);
                getDesigner().delete(aoImage);
                //refreshPreview?
                loadItems(Optional.empty());
            }
        });
        toolTip = new TextTooltip("Delete Image", SKIN);
        delete.addListener(toolTip);
        buttons.add(delete).right();
        table.add(buttons).growX().row();

        Container bg = new Container();
        bg.setClip(true);
        Image image = new Image(drawables.get(aoImage));
        image.setScaling(Scaling.fit);
        bg.fill(false);
        bg.setActor(image);
        table.add(bg).expand().center().row();
        table.add(new Label(aoImage.getId() + "- file: " + aoImage.getFileNum(), SKIN));

        table.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selected = table;
            }
        });
        return table;
    }

    @Override
    public void filesDropped(List<FileHandle> files) {
        files.forEach(getDesigner()::create);
        loadItems(Optional.empty());
    }

    @Override
    Preview<AOImage> createPreview() {
        return null;
    }

    @Override
    Editor<AOImage> createItemView() {
        return null;
    }

    @Override
    protected void sort(Array<AOImage> items) {
        items.sort(Comparator.comparingInt(AOImage::getId));
    }

    @Override
    protected void keyPressed(int keyCode) {
    }

    public void clearUsedImages() {
        usedImages.clear();
    }
}
