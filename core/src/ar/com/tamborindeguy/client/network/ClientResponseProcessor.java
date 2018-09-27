package ar.com.tamborindeguy.client.network;

import ar.com.tamborindeguy.client.screens.GameScreen;
import ar.com.tamborindeguy.client.systems.physics.MovementProcessorSystem;
import ar.com.tamborindeguy.network.combat.AttackResponse;
import ar.com.tamborindeguy.network.interfaces.IResponseProcessor;
import ar.com.tamborindeguy.network.login.LoginFailed;
import ar.com.tamborindeguy.network.login.LoginOK;
import ar.com.tamborindeguy.network.movement.MovementResponse;
import com.badlogic.gdx.Gdx;

import static com.artemis.E.E;

public class ClientResponseProcessor implements IResponseProcessor {

    @Override
    public void processResponse(LoginOK response) {
        Gdx.app.postRunnable(() -> {
            GameScreen.game.showGameScreen();
            GameScreen.client.loginFinished();
        });
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

    @Override
    public void processResponse(AttackResponse attackResponse) {
        // TODO show feedback on console?
    }

}
