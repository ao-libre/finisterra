package server.systems.account;

import com.artemis.annotations.Wire;
import com.esotericsoftware.jsonbeans.JsonReader;
import com.esotericsoftware.jsonbeans.JsonValue;
import com.esotericsoftware.minlog.Log;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import server.database.Account;
import server.database.Charfile;
import server.systems.network.ServerSystem;
import shared.network.account.AccountCreationResponse;
import shared.network.account.AccountLoginResponse;
import shared.util.AccountSystemUtilities;
import shared.util.Messages;

import java.io.File;
import java.util.ArrayList;

@Wire
public class AccountSystem extends PassiveSystem {

    private ServerSystem serverSystem;
    private UserSystem userSystem;

    public static void checkStorageDirectory() {
        File accountDir = new File(Account.DIR_CUENTAS);
        if (!accountDir.isDirectory())
            accountDir.mkdirs();
    }

    public void createAccount(int connectionID, String username, String email, String password) {
        // Hasheamos la contraseña.
        String hashedPassword = AccountSystemUtilities.hashPassword(password);

        // Resultado de la operacion.
        boolean successful = false; //@todo todos los requests podrían llevar un flag de exito/error

        if (!Account.exists(email)) {
            // Guardamos la cuenta.
            try {
                Account account = new Account(username, email, hashedPassword);
                account.save();
                successful = true;
            } catch (Exception ex) {
                Log.info("Creacion de cuentas", "No se pudo crear la cuenta: " + email, ex);
            }
        }

        serverSystem.sendTo(connectionID, new AccountCreationResponse(successful));
        serverSystem.closeConnection(connectionID);
    }

    public void loginAccount(int connectionID, String email, String password) {
        // Obtenemos la cuenta de la carpeta Accounts.
        Account requestedAccount = Account.load(email);

        if (requestedAccount == null) {
            serverSystem.sendTo(connectionID, new AccountLoginResponse(Messages.NON_EXISTENT_ACCOUNT));
            serverSystem.closeConnection(connectionID);
            return;
        } else if (!AccountSystemUtilities.checkPassword(password, requestedAccount.getPassword())) {
            serverSystem.sendTo(connectionID, new AccountLoginResponse(Messages.ACCOUNT_LOGIN_FAILED));
            serverSystem.closeConnection(connectionID);
            return;
        }

        String username = requestedAccount.getUsername();

        ArrayList<String> characters;
        if (requestedAccount.getCharacters().isEmpty()) {
            Log.info("******** La cuenta " + username + " no tiene PJ creando lista");
            for (int i = 0; i < 6; i++) {
                requestedAccount.addCharacter("", i);
            }
        }
        characters = requestedAccount.getCharacters();

        // todo recuperar el heroID
        ArrayList<Integer> charactersData = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            charactersData.add(-1);
        }

        if (!characters.isEmpty()) {
            for (int i = 0; i < 6; i++) {
                if (!characters.get(i).isEmpty()) {
                    String name = characters.get(i);
                    File file = new File(Charfile.DIR_CHARFILES + name + ".json");
                    Log.info("*** obteniendo hero id del pj " + name);
                    if (file.isFile() && file.canRead()) {
                        // leer los datos del archivo
                        try {
                            JsonValue jsonData = new JsonReader().parse(file);
                            JsonValue heroIdData = jsonData.get("component.entity.character.info.CharHero");
                            JsonValue hpData = jsonData.get("component.entity.character.status.Health");
                            JsonValue mpData = jsonData.get("component.entity.character.status.Mana");
                            int heroid = heroIdData.getInt("heroId");
                            int hpMin = hpData.getInt("min");
                            int hpMax = hpData.getInt("max");
                            int mpMin = mpData.getInt("min");
                            int mpMax = mpData.getInt("min");
                            // asigna los valores
                            charactersData.set(i, heroid);
                            charactersData.set(i + 6, hpMin);
                            charactersData.set(i + 12, hpMax);
                            charactersData.set(i + 18, mpMin);
                            charactersData.set(i + 24, mpMax);
                        } catch (Exception ex) {
                            Log.info("error al tratar de leer el personaje " + name);
                        }

                    } else {
                        Log.info("error al tratar de leer el personaje " + name);
                    }
                }
            }
        }

        serverSystem.sendTo(connectionID, new AccountLoginResponse(email, characters, charactersData));
    }

    public Account getAccount(String email) {
        Account requestedAccount = Account.load(email);
        Log.info("***** enviando datos de la cuenta " + requestedAccount.getUsername());
        return requestedAccount;
    }
}
