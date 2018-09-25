package ar.com.tamborindeguy.client.network;

import ar.com.tamborindeguy.client.game.AO;
import ar.com.tamborindeguy.client.screens.GameScreen;
import ar.com.tamborindeguy.client.systems.physics.MovementProcessorSystem;
import ar.com.tamborindeguy.network.interfaces.IResponseProcessor;
import ar.com.tamborindeguy.network.login.LoginFailed;
import ar.com.tamborindeguy.network.login.LoginOK;
import ar.com.tamborindeguy.network.movement.MovementResponse;

import static com.artemis.E.E;

public class ClientResponseProcessor implements IResponseProcessor {

    private AO ao;

    public ClientResponseProcessor(AO ao) {
        this.ao = ao;
    }

    @Override
    public void processResponse(LoginOK response) {
//        ClientSystem.notificationProcessor.processNotification(new EntityUpdate(response.entityId, response.player.getComponents()));
        int localEntity = GameScreen.getNetworkedEntity(response.entityId);
        GameScreen.setPlayer(localEntity);
        E(localEntity).focused();
    }

    @Override
    public void processResponse(LoginFailed response) {

    }

    @Override
    public void processResponse(MovementResponse movementResponse) {
        MovementProcessorSystem.validateRequest(movementResponse.requestNumber, movementResponse.destination);
    }

}
