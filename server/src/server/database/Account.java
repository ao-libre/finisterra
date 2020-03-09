package server.database;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.esotericsoftware.minlog.Log;
import shared.util.AOJson;

import java.util.ArrayList;

public class Account {

    /**
     * Fields excluidos de la serializacion.
     *
     * Para excluir un field tenes que declararlo como "transient".
     */
    static transient final Json json = new AOJson();

    /**
     * Fields que serán serializados.
     */
    private String email;
    private String password;
    private boolean banned;
    private ArrayList<String> personajes = new ArrayList<>();

    public Account() {
    }

    public Account(String email, String password) {
        this.email = email;
        this.password = password;

        this.banned = false;
    }


    public static Account load(String email) {
        try  {
            return json.fromJson(Account.class, Gdx.files.local("Accounts/" + email + ".json"));
        } catch (Exception ex) {
            //Log.error("Accounts" , "Account not found!", ex);
        }

        return null;
    }

    public void save() {
        json.toJson(this, new FileHandle("Accounts/" + this.email + ".json"));
    }

    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public boolean getBanned() { return banned; }
    public ArrayList<String> getPersonajes() { return personajes; }

}