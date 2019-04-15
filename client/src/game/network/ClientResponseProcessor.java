package game.network;

import com.badlogic.gdx.Gdx;
import game.AOGame;
import game.screens.LobbyScreen;
import game.screens.LoginScreen;
import game.screens.RoomScreen;
import game.systems.physics.MovementProcessorSystem;
import shared.network.combat.AttackResponse;
import shared.network.interfaces.IResponseProcessor;
import shared.network.lobby.CreateRoomResponse;
import shared.network.lobby.JoinLobbyResponse;
import shared.network.lobby.JoinRoomResponse;
import shared.network.lobby.StartGameResponse;
import shared.network.login.LoginFailed;
import shared.network.login.LoginOK;
import shared.network.movement.MovementResponse;

public class ClientResponseProcessor implements IResponseProcessor {


    @Override
    public void processResponse(LoginOK response) {

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

    @Override
    public void processResponse(CreateRoomResponse createRoomResponse) {
        Gdx.app.postRunnable(() -> {
            AOGame game = (AOGame) Gdx.app.getApplicationListener();
            LobbyScreen lobby = (LobbyScreen) game.getScreen();
            game.toRoom(lobby.getClientSystem(), createRoomResponse.getRoom(), createRoomResponse.getPlayer());
        });
    }

    @Override
    public void processResponse(JoinLobbyResponse joinLobbyResponse) {
        Gdx.app.postRunnable(() -> {
            AOGame game = (AOGame) Gdx.app.getApplicationListener();
            LoginScreen login = (LoginScreen) game.getScreen();
            game.toLobby(joinLobbyResponse.getPlayer(), joinLobbyResponse.getRooms(), login.getClientSystem());
        });
    }

    @Override
    public void processResponse(JoinRoomResponse joinRoomResponse) {
        Gdx.app.postRunnable(() -> {
            AOGame game = (AOGame) Gdx.app.getApplicationListener();
            LobbyScreen lobby = (LobbyScreen) game.getScreen();
            game.toRoom(lobby.getClientSystem(), joinRoomResponse.getRoom(), joinRoomResponse.getPlayer());
        });
    }

    @Override
    public void processResponse(StartGameResponse startGameResponse) {
        Gdx.app.postRunnable(() -> {
            AOGame game = (AOGame) Gdx.app.getApplicationListener();
            if (game.getScreen() instanceof RoomScreen) {
                RoomScreen roomScreen = (RoomScreen) game.getScreen();
                game.toGame(startGameResponse.getHost(), startGameResponse.getTcpPort(), roomScreen.getPlayer());
            }
        });
    }

}
