package design.screens.views;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import design.designers.ImageDesigner;
import design.designers.ObjectDesigner;
import design.editors.GenericEditor;
import design.editors.fields.FieldEditor;
import design.screens.ScreenEnum;
import design.graphic.AOImageActor;
import model.textures.AOImage;
import org.jetbrains.annotations.NotNull;
import shared.objects.types.Obj;

import static launcher.DesignCenter.SKIN;

public class ObjectView extends View<Obj, ObjectDesigner> {

    public ObjectView() {
        super(new ObjectDesigner());
    }

    @Override
    Preview<Obj> createPreview() {
        return new ObjPreview();
    }

    @Override
    Editor<Obj> createItemView() {
        return new ObjItem();
    }

    @Override
    protected void sort(Array<Obj> items) {

    }

    @Override
    protected void keyPressed(int keyCode) {

    }

    private class ObjPreview extends Preview<Obj> {

        private final Table content;
        private Obj obj;

        ObjPreview() {
            super(SKIN);
            content = new Table();
            add(content);
        }

        @Override
        void show(Obj obj) {
            this.obj = obj;
            content.clear();
            int index = obj.getGrhIndex();
            ImageView screen = (ImageView) ScreenEnum.IMAGE_VIEW.getScreen();
            ImageDesigner designer = screen.getDesigner();
            AOImage aoImage = designer.get(index).orElse(new AOImage());
            content.add(new AOImageActor(aoImage, getAnimationHandler()));
        }

        @Override
        Obj get() {
            return obj;
        }
    }

    private class ObjItem extends Editor<Obj> {

        ObjItem() {
            super(SKIN);
        }

        @NotNull
        @Override
        protected Table getTable(FieldEditor.FieldListener listener) {
            return GenericEditor.getTable(current, listener);
        }

        @Override
        protected Obj getCopy(Obj to) {
            return to;
        }
    }
}
