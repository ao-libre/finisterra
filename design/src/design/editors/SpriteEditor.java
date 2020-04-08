package design.editors;

import com.badlogic.gdx.files.FileHandle;
import design.editors.utils.SliceResult;
import design.editors.utils.Slicer;
import component.entity.world.Dialog;

public class SpriteEditor extends Dialog {

    public SliceResult slice(FileHandle file, int startingId) {
        return new Slicer(file).slice(startingId);
    }


}
