package server.database;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import shared.util.AOJson;

public class Charfile {

    /**
     * Fields excluidos de la serializacion.
     *
     * Para excluir un field tenes que declararlo como "transient".
     */
    static transient final Json json = new AOJson();

    /**
     * Fields que serán serializados.
     */
    public String nick;
    public String email;
    public String clase;

    public int Head = 0;
    public int Arma = 0;
    public int Body = 0;
    public int Casco = 0;
    public int Escudo = 0;

    public int Heading = 0;
    public int Hogar = 0;
    public String Descripcion = "";
    public String genero = "";
    public double TiempoOnline = 0;
    public String Posicion = "1-50-50";
    public String LastIP = "127.0.0.1";
    public boolean Online = false;

    public Charfile() { }

    public static Charfile load(String nick) {
        try  {
            return json.fromJson(Charfile.class, Gdx.files.local("Charfiles/" + nick + ".json"));
        } catch (Exception ex) {
            //Log.error("Charfile" , "Charfile not found!", ex);
        }

        return null;
    }

    public void save() {
        json.toJson(this, new FileHandle("Charfiles/" + this.nick + ".json"));
    }

}
