package server.database;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import shared.util.AOJson;

import java.util.ArrayList;

public class Charfile {

    static transient final Json json = new AOJson();

    public String nick;
    public String email;
    public String clase;

    public enum Genero {
        Hombre,
        Mujer
    }

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

    public static class Inventory {

        public static class Slot {

            public int Anillo = 0;
            public int Armadura = 0;
            public int Barco = 0;
            public int Casco = 0;
            public int Escudo = 0;
            public int Arma = 0;

        }

        public ArrayList<Integer> Objects = new ArrayList<>();
        public ArrayList<Integer> Inventory = new ArrayList<>();
        public ArrayList<Integer> BancoInventory = new ArrayList<>();

    }

    public static class Hechizos {

        public ArrayList<Integer> Hechizos = new ArrayList<>();

    }

    public static class Atributos {

        public short Vida = 0;
        public short Mana = 0;
        public short Energia = 0;

        public short Fuerza = 0;
        public short Agilidad = 0;
        public short Constitucion = 0;
        public short Inteligencia = 0;

    }

    public static class Facciones {

        public short CiudadanosMatados = 0;
        public short CriminalesMatados = 0;
        public short EjercitoCaos = 0;
        public short EjercitoReal = 0;

        public short FechaIngreso = 0;
        public short MatadosIngreso = 0;
        public short ProximaRecompensa = 0;
        public short NivelIngreso = 0;

        public short Reenlistadas = 0;
    }

    public static class Estados {

        public boolean Ban = false;
        public boolean Desnudo = false;
        public boolean Envenenado = false;
        public boolean Escondido = false;
        public boolean Hambre = false;
        public boolean Sed = false;
        public int UltimoMapa = 1;
        public boolean Muerto = false;
        public boolean Navegando = false;
        public boolean Paralizado = false;

    }

    public static class Contadores {

        public int Pena = 0;
        public int SkillsAsignados = 0;

    }

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
