package server.database;

import com.artemis.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import shared.objects.factory.POJO;
import shared.util.AOJson;

import java.util.Collection;

public class Charfile extends POJO {

    /**
     * Fields excluidos de la serializacion.
     *
     * Para excluir un field tenes que declararlo como "transient".
     */
    static transient final Json json = new AOJson();

    /**
     * Fields que serán serializados.
     */

    public static transient final String DIR_CHARFILE = "Charfile/";

    public Collection<? extends Component> components;


    public Charfile() { }


    // se utiliza POJO para leer, grabar y actualizar json
    public static boolean exists(String name){
        return POJO.exists(DIR_CHARFILE + name + POJO.EXTENSION);
    }

    public static Charfile load(String name){
        return POJO.load(Charfile.class, DIR_CHARFILE + name + POJO.EXTENSION);
    }

    public void save(String name) {
        super.save(this, DIR_CHARFILE + name + POJO.EXTENSION);
    }

    public void update(String name) {
        // Misma cosa, distinto nombre para que se entienda mejor.
        super.save(this, DIR_CHARFILE + name + POJO.EXTENSION);
    }

}
