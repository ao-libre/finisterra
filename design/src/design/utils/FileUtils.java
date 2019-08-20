package design.utils;

import design.editors.utils.Utils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.io.File;

import static org.lwjgl.system.MemoryStack.stackPush;

public class FileUtils {


    public static File openDialog(String title, String defaultPath,
                                  String[] filterPatterns, String filterDescription) {
        String result;

        //fix file path characters
        if (Utils.isWindows()) {
            defaultPath = defaultPath.replace("/", "\\");
        } else {
            defaultPath = defaultPath.replace("\\", "/");
        }

        if (filterPatterns != null && filterPatterns.length > 0) {
            try (MemoryStack stack = stackPush()) {
                PointerBuffer pointerBuffer = stack.mallocPointer(filterPatterns.length);

                for (String filterPattern : filterPatterns) {
                    pointerBuffer.put(stack.UTF8(filterPattern));
                }
                pointerBuffer.flip();
                result = TinyFileDialogs.tinyfd_openFileDialog(title, defaultPath, pointerBuffer, filterDescription, false);
            }
        } else {
            result = TinyFileDialogs.tinyfd_openFileDialog(title, defaultPath, null, filterDescription, false);
        }

        if (result != null) {
            return new File(result);
        } else {
            return null;
        }
    }

    public static String selectFolder(String title) {
        return TinyFileDialogs.tinyfd_selectFolderDialog(title, "");
    }

}
