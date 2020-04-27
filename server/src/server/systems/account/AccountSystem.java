package server.systems.account;

import com.artemis.annotations.Wire;
import com.esotericsoftware.minlog.Log;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import server.database.Account;
import server.systems.network.ServerSystem;
import shared.network.account.AccountCreationResponse;
import shared.network.account.AccountLoginResponse;
import shared.util.AccountSystemUtilities;

import java.util.ArrayList;

@Wire
public class AccountSystem extends PassiveSystem {

    private ServerSystem serverSystem;
    private UserSystem userSystem;

    public void createAccount(int connectionId, String username, String email, String password) {

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

        serverSystem.sendTo(connectionId, new AccountCreationResponse(successful));
    }

    public void login(int connectionId, String email, String password) {
        // Obtenemos la cuenta de la carpeta Accounts.
        Account requestedAccount = Account.load(email);

        boolean successful = (requestedAccount != null) && (AccountSystemUtilities.checkPassword(password, requestedAccount.getPassword()));

        String username = successful ? requestedAccount.getUsername() : null;
        ArrayList<String> characters = new ArrayList<>();
        if (successful){
            if(requestedAccount.getCharacters().isEmpty()) {
                Log.info("********la cuenta " +requestedAccount.getUsername() +"no tiene pj creando lista" );
                for (int i = 0;i<6;i++) {
                    requestedAccount.addCharacter( "", i );
                }
            }
            characters = requestedAccount.getCharacters();
        } else {
           characters = null;
        }

        /* todo recuperar el heroID
        ArrayList<String> charHeroID = new ArrayList<String>();
        if (!characters.isEmpty()) {
            for (int i = 0; i < 6; i++) {
                userSystem.loadUser(characters.get( i )).get();
                charHeroID.add(i,);
            }
        }
         */
        serverSystem.sendTo(connectionId, new AccountLoginResponse(email, successful,characters ));
    }
    public Account getAccount(String email){
        Account requestedAccount = Account.load(email);
        Log.info("***** enviando datos de la cuenta " + requestedAccount.getUsername());
        return requestedAccount;
    }
}
