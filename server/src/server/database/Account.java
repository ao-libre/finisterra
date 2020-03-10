package server.database;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
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
    private ArrayList<String> characters = new ArrayList<>();

    public Account() {
    }

    public Account(String email, String password) {
        this.email = email;
        this.password = password;
        this.banned = false;
    }

    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public boolean isBanned() { return banned; }
    public ArrayList<String> getCharacters() { return characters; }
    public void addCharacter(String character) { characters.add(character); }

    /* @todo abstraer estos métodos en una clase de base de datos generica */
    public static boolean exists(String email) {
        boolean exists = true;
        try {
            Gdx.files.local("Accounts/" + email + ".json");
        } catch (GdxRuntimeException ex) {
            exists = false;
        }
        return exists;
    }

    public static Account load(String email) {
        Account account = null;
        try  {
            account = json.fromJson(Account.class, Gdx.files.local("Accounts/" + email + ".json"));
        } catch (GdxRuntimeException ex) {
            //Log.error("Accounts" , "Account not found!", ex);
        } catch (Exception ex) {
            //Error de serialización, etc. Reportar al programador
        }
        return account;
    }

    public void create() {
        json.toJson(this, new FileHandle("Accounts/" + this.email + ".json"));
    }

    public void update() {
        json.toJson(this, new FileHandle("Accounts/" + this.email + ".json"));
    }
}