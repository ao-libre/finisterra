package game.network;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.utils.TimeUtils;
import com.esotericsoftware.minlog.Log;
import game.AOGame;
import game.screens.*;
import game.systems.network.ClientSystem;
import game.systems.network.TimeSync;
import game.systems.physics.MovementProcessorSystem;
import shared.network.account.AccountCreationResponse;
import shared.network.account.AccountLoginResponse;
import shared.network.interfaces.IResponseProcessor;
import shared.network.lobby.*;
import shared.network.lobby.player.PlayerLoginRequest;
import shared.network.movement.MovementResponse;
import shared.network.time.TimeSyncResponse;

@Wire
public class ClientResponseProcessor extends BaseSystem implements IResponseProcessor {

    private TimeSync timeSync;

    @Override
    public void processResponse(MovementResponse movementResponse) {
        MovementProcessorSystem.validateRequest(movementResponse.requestNumber, movementResponse.destination);
    }

    @Override
    public void processResponse(CreateRoomResponse createRoomResponse) {
        AOGame game = (AOGame) Gdx.app.getApplicationListener();
        LobbyScreen lobby = (LobbyScreen) game.getScreen();

        switch (createRoomResponse.getStatus()) {
            case CREATED:
                game.toRoom(lobby.getClientSystem(), createRoomResponse.getRoom(), createRoomResponse.getPlayer());
                break;
            case MAX_ROOM_LIMIT:
                lobby.roomMaxLimit();
                break;
        }
    }

    @Override
    public void processResponse(JoinLobbyResponse joinLobbyResponse) {
        AOGame game = (AOGame) Gdx.app.getApplicationListener();
        LoginScreen login = (LoginScreen) game.getScreen();
        game.toLobby(joinLobbyResponse.getPlayer(), joinLobbyResponse.getRooms(), login.getClientSystem());
    }

    @Override
    public void processResponse(JoinRoomResponse joinRoomResponse) {
        AOGame game = (AOGame) Gdx.app.getApplicationListener();
        LobbyScreen lobby = (LobbyScreen) game.getScreen();
        game.toRoom(lobby.getClientSystem(), joinRoomResponse.getRoom(), joinRoomResponse.getPlayer());
    }

    @Override
    public void processResponse(StartGameResponse startGameResponse) {
        AOGame game = (AOGame) Gdx.app.getApplicationListener();
        if (game.getScreen() instanceof RoomScreen) {
            RoomScreen roomScreen = (RoomScreen) game.getScreen();
            GameScreen gameScreen = (GameScreen) ScreenEnum.GAME.getScreen(game.getClientConfiguration(), game.getAssetManager());
            ClientSystem clientSystem = new ClientSystem(startGameResponse.getHost(), startGameResponse.getTcpPort());
            clientSystem.start();
            gameScreen.initWorld(clientSystem);
            clientSystem.getKryonetClient().sendToAll(new PlayerLoginRequest(roomScreen.getPlayer()));
            game.toGame(gameScreen);
        }
    }

    @Override
    public void processResponse(TimeSyncResponse timeSyncResponse) {
        timeSync.receive(timeSyncResponse);
        Log.info("Local timestamp: " + TimeUtils.millis() / 1000);
        Log.info("RTT: " + timeSync.getRtt() / 1000);
        Log.info("Time offset: " + timeSync.getTimeOffset() / 1000);
    }

    @Override
    public void processResponse(AccountCreationResponse accountCreationResponse) {
        AOGame game = (AOGame) Gdx.app.getApplicationListener();
        AbstractScreen screen = (AbstractScreen) game.getScreen();

        if (accountCreationResponse.isSuccessful()) {
            game.toLogin();
            Dialog dialog = new Dialog("Exito", screen.getSkin());
            dialog.text("Cuenta creada con exito");
            dialog.button("OK");
            dialog.show(screen.getStage());
        }
        else {
            Dialog dialog = new Dialog("Error", screen.getSkin());
            dialog.text("Error al crear la cuenta");
            dialog.button("OK");
            dialog.show(screen.getStage());
        }
    }

    @Override
    public void processResponse(AccountLoginResponse accountLoginResponse) {
        AOGame game = (AOGame) Gdx.app.getApplicationListener();
        LoginScreen screen = (LoginScreen) game.getScreen();

        if (accountLoginResponse.isSuccessful()) {
            /*
            Dialog dialog = new Dialog("Exito", screen.getSkin());
            dialog.text("Logueado con exito");
            dialog.button("OK");
            dialog.show(screen.getStage());
            */

            //@todo pasar al lobby del servidor
            //hotfix para recuperar funcionalidad
            screen.getClientSystem().getKryonetClient().sendToAll(new JoinLobbyRequest(accountLoginResponse.getUsername()));
        }
        else {
            Dialog dialog = new Dialog("Error", screen.getSkin());
            dialog.text("Error al loguearse");
            dialog.button("OK");
            dialog.show(screen.getStage());
        }
    }

    @Override
    protected void processSystem() {

    }
}
