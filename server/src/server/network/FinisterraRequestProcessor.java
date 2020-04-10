package server.network;

import com.esotericsoftware.minlog.Log;
import server.database.Account;
import server.systems.FinisterraSystem;
import shared.network.account.AccountCreationRequest;
import shared.network.account.AccountCreationResponse;
import shared.network.account.AccountLoginRequest;
import shared.network.account.AccountLoginResponse;
import shared.network.interfaces.DefaultRequestProcessor;
import shared.util.AccountSystemUtilities;

/**
 * Every packet received from users will be processed here
 */
public class FinisterraRequestProcessor extends DefaultRequestProcessor {

    private FinisterraSystem networkManager;

}
