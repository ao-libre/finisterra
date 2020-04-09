package design.screens.views;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import design.designers.ImageDesigner;
import design.designers.TileSetDesigner;
import design.editors.TileSetEditor;
import design.editors.fields.FieldEditor;
import design.screens.ScreenEnum;
import design.screens.map.model.TileSet;
import design.graphic.AOImageActor;
import model.textures.AOImage;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static launcher.DesignCenter.SKIN;

public class TileSetView extends View<TileSet, TileSetDesigner> {

    public TileSetView() {
        super(new TileSetDesigner());
    }

    @Override
    Preview<TileSet> createPreview() {
        return new TileSetPreview();
    }

    @Override
    Editor<TileSet> createItemView() {
        return new TileSetItem();
    }

    @Override
    protected void sort(Array<TileSet> items) {
        items.sort(Comparator.comparingInt(TileSet::getId));
    }

    @Override
    protected void keyPressed(int keyCode) {

    }

    @Override
    public void filesDropped(List<FileHandle> files) {
        List<TileSet> list = files.stream().map(file -> getDesigner().create(file)).collect(Collectors.toList());
        loadItems(Optional.ofNullable(list.get(0)));
    }

    class TileSetItem extends Editor<TileSet> {

        public TileSetItem() {
            super(SKIN);
        }

        @NotNull
        @Override
        protected Table getTable(FieldEditor.FieldListener listener) {
            return TileSetEditor.getTable(current, listener);
        }

        @Override
        protected TileSet getCopy(TileSet to) {
            return new TileSet(to);
        }
    }

    class TileSetPreview extends Preview<TileSet> {

        private final Table content;
        private TileSet current;

        public TileSetPreview() {
            super(SKIN);
            content = new Table();
            add(content);
        }

        @Override
        void show(TileSet tileSet) {
            this.current = tileSet;
            content.clear();
            if (tileSet == null) {
                return;
            }
            ImageView screen = (ImageView) ScreenEnum.IMAGE_VIEW.getScreen();
            ImageDesigner designer = screen.getDesigner();
            for (int i = 0; i < tileSet.getRows(); i++) {
                for (int j = 0; j < tileSet.getCols(); j++) {
                    int image = tileSet.getImage(j, i);
                    AOImage aoImage = designer.get(image).orElse(new AOImage());
                    content.add(new AOImageActor(aoImage, getAnimationHandler()));
                }
                content.row();
            }

        }

        @Override
        TileSet get() {
            return current;
        }
    }
}
