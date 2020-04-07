package game.systems.network;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.utils.TimeUtils;
import com.esotericsoftware.minlog.Log;
import game.AOGame;
import game.screens.*;
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

    private ScreenManager screenManager;
    private ClientSystem clientSystem;
    private TimeSync timeSync;
    private MovementProcessorSystem movementProcessorSystem;

    @Override
    public void processResponse(MovementResponse movementResponse) {
        movementProcessorSystem.validateRequest(movementResponse.requestNumber, movementResponse.destination);
    }

    @Override
    public void processResponse(CreateRoomResponse createRoomResponse) {
        LobbyScreen lobby = (LobbyScreen) game.getScreen();

        switch (createRoomResponse.getStatus()) {
            case CREATED:
                screenManager.toRoom(createRoomResponse.getRoom(), createRoomResponse.getPlayer());
                break;
            case MAX_ROOM_LIMIT:
                lobby.roomMaxLimit();
                break;
        }
    }

    @Override
    public void processResponse(JoinLobbyResponse joinLobbyResponse) {
        screenManager.toLobby(joinLobbyResponse.getPlayer(), joinLobbyResponse.getRooms());
    }

    @Override
    public void processResponse(JoinRoomResponse joinRoomResponse) {
        screenManager.toRoom(joinRoomResponse.getRoom(), joinRoomResponse.getPlayer());
    }

    @Override
    public void processResponse(StartGameResponse startGameResponse) {
        AOGame game = (AOGame) Gdx.app.getApplicationListener();
        if (game.getScreen() instanceof RoomScreen) {
            RoomScreen roomScreen = (RoomScreen) game.getScreen();
            GameScreen gameScreen = (GameScreen) ScreenEnum.GAME.getScreen(game.getWorld());
            clientSystem.send(new PlayerLoginRequest(roomScreen.getPlayer()));
            game.toGame(gameScreen);
        }
    }

    @Override
    public void processResponse(TimeSyncResponse timeSyncResponse) {
        timeSync.receive(timeSyncResponse);
        Log.debug("Local timestamp: " + TimeUtils.millis() / 1000);
        Log.debug("RTT: " + timeSync.getRtt() / 1000);
        Log.debug("Time offset: " + timeSync.getTimeOffset() / 1000);
    }

    @Override
    public void processResponse(AccountCreationResponse accountCreationResponse) {
        AbstractScreen screen = (AbstractScreen) game.getScreen();

        if (accountCreationResponse.isSuccessful()) {
            screenManager.toLogin();
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
            clientSystem.send(new JoinLobbyRequest(accountLoginResponse.getUsername()));
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
