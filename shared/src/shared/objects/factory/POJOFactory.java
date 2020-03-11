package shared.objects.factory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.esotericsoftware.minlog.Log;
import shared.util.AOJson;

import java.io.File;
import java.io.FileInputStream;

public abstract class POJOFactory {

    /**
     * Fields excluidos de la serializacion.
     *
     * Para excluir un field tenes que declararlo como "transient".
     */
    private static transient final Json json = new AOJson();
    public static transient final String EXTENSION = ".json";

    public static <T> T load(Class<T> Class, String path) {
        T result = null;
        try (FileInputStream is = new FileInputStream(path))  {
            result = json.fromJson(Class, is);
        } catch (GdxRuntimeException ex) {
            Log.error("POJO File load" , "File not found!", ex);
        } catch (Exception ex) {
            Log.info("Ha ocurrido un error al LEER este POJO: " + path, ex);
        }

        return result;
    }

    public void save(Class<?> Class, String path) {
        try {
            json.toJson(Class, new FileHandle(path));
        } catch (Exception ex) {
            Log.info("Ha ocurrido un error al GUARDAR este POJO: " + path, ex);
        }
    }

    public static boolean exists(String path) {
        File file = new File(path);
        return file.isFile() && file.canRead();
    }
}
