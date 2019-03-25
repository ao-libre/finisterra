package game.network;

import game.AOGame;
import game.screens.GameScreen;
import game.systems.physics.MovementProcessorSystem;
import game.managers.WorldManager;
import shared.network.combat.AttackResponse;
import shared.network.interfaces.IResponseProcessor;
import shared.network.login.LoginFailed;
import shared.network.login.LoginOK;
import shared.network.movement.MovementResponse;
import com.badlogic.gdx.Gdx;

import static com.artemis.E.E;

public class ClientResponseProcessor implements IResponseProcessor {

    @Override
    public void processResponse(LoginOK response) {
        // TODO: Why huge runnable? There's a lot of init happening in .showGameScreen() (== screen.show()) that blocks other methods
        Gdx.app.postRunnable(() -> {
            AOGame game = (AOGame) Gdx.app.getApplicationListener();
            game.showGameScreen();
            game.getClientSystem().loginFinished(); // TODO: Could be done outside runnable
            int localEntity = WorldManager.getNetworkedEntity(response.entityId);
            GameScreen.setPlayer(localEntity);
            // focus player
            E(localEntity).focused();
        });
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
