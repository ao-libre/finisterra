package design.editors;

import com.badlogic.gdx.files.FileHandle;
import component.entity.world.Dialog;
import design.editors.utils.SliceResult;
import design.editors.utils.Slicer;

public class SpriteEditor extends Dialog {

    public SliceResult slice(FileHandle file, int startingId) {
        return new Slicer(file).slice(startingId);
    }


}
