package shared.objects.factory;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.esotericsoftware.minlog.Log;
import shared.util.AOJson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

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
        } catch (FileNotFoundException ex) {
            Log.error("Carga de POJO's" , "Archivo no encontrado! Archivo: " + path, ex);
        } catch (Exception ex) {
            Log.info("Error al LEER este POJO: " + path, ex);
        }

        return result;
    }

    public void save(Object object, String path) {
        try {
            json.toJson(object, new FileHandle(path));
        } catch (Exception ex) {
            Log.info("Error al GUARDAR este POJO: " + path, ex);
        }
    }

    public static boolean exists(String path) {
        File file = new File(path);
        return file.isFile() && file.canRead();
    }
}
