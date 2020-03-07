package server.database;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.esotericsoftware.minlog.Log;
import shared.util.AOJson;

import java.util.ArrayList;

public class Account {
    private String email;
    private String password;
    private String salt;
    private boolean banned;
    private ArrayList<String> personajes = new ArrayList<>();

    public Account() {
    }

    public Account(String email, String password, String salt) {
        this.email = email;
        this.password = password;
        this.salt = salt;

        this.banned = false;
    }

    public static Account load(String email) {
        Json accountJson = new AOJson();
        try  {
            return accountJson.fromJson(Account.class, Gdx.files.local("Accounts/" + email + ".json"));
        } catch (Exception ex) {
            Log.error("Accounts" , "Account not found!", ex);
        }

        return null;
    }

    public void save() {
        Json json = new AOJson();
        json.toJson(this, new FileHandle("Accounts/" + this.email + ".json"));
    }

    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getSalt() { return salt; }
    public boolean getBanned() { return banned; }
    public ArrayList<String> getPersonajes() { return personajes; }

}