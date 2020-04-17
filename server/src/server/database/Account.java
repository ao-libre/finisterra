package server.database;

import com.esotericsoftware.minlog.Log;
import shared.objects.factory.POJO;

import java.util.ArrayList;

public class Account extends POJO {

    /**
     * Fields excluidos de la serializacion.
     *
     * Para excluir un field tenes que declararlo como "transient".
     */
    public static transient final String DIR_CUENTAS = "Accounts/";

    /**
     * Fields que serán serializados.
     */
    private String username;
    private String email;
    private String password;
    private boolean banned;
    private ArrayList<String> characters = new ArrayList<>();

    public Account() {
    }

    public Account(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.banned = false;
        for (int i =0; i<6;i++){
            characters.add( "" );
        }
    }

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public boolean isBanned() { return banned; }
    public ArrayList<String> getCharacters() { return characters; }
    public void addCharacter(String character, int index) {
        characters.add(index, character);
        Log.info("agregado el pj " +character + "en la posicon " +index + "a la cuenta");
        update();
    }

    public static boolean exists(String email){
        return POJO.exists(DIR_CUENTAS + email + POJO.EXTENSION);
    }

    public static Account load(String email){
        return POJO.load(Account.class, DIR_CUENTAS + email + POJO.EXTENSION);
    }

    public void save() {
        super.save(this, DIR_CUENTAS + this.email + POJO.EXTENSION);
    }

    public void update() {
        // Misma cosa, distinto nombre para que se entienda mejor.
        super.save(this, DIR_CUENTAS + this.email + POJO.EXTENSION);
    }
}