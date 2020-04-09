package game.systems.network;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.utils.TimeUtils;
import com.esotericsoftware.minlog.Log;
import game.screens.*;
import game.systems.lobby.LobbySystem;
import game.systems.physics.MovementProcessorSystem;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import shared.network.account.AccountCreationResponse;
import shared.network.account.AccountLoginResponse;
import shared.network.interfaces.IResponseProcessor;
import shared.network.lobby.*;
import shared.network.lobby.player.PlayerLoginRequest;
import shared.network.movement.MovementResponse;
import shared.network.time.TimeSyncResponse;

@Wire
public class ClientResponseProcessor extends PassiveSystem implements IResponseProcessor {

    private ClientSystem clientSystem;
    private LobbySystem lobbySystem;
    private MovementProcessorSystem movementProcessorSystem;
    private ScreenManager screenManager;
    private TimeSync timeSync;

    @Override
    public void processResponse(MovementResponse movementResponse) {
        movementProcessorSystem.validateRequest(movementResponse.requestNumber, movementResponse.destination);
    }

    @Override
    public void processResponse(CreateRoomResponse createRoomResponse) {
        switch (createRoomResponse.getStatus()) {
            case CREATED:
                lobbySystem.setCurrentRoom(createRoomResponse.getRoom());
                lobbySystem.setPlayer(createRoomResponse.getPlayer());
                screenManager.to(ScreenEnum.ROOM);
                break;
            case MAX_ROOM_LIMIT:
                lobbySystem.roomMaxLimit();
                break;
        }
    }

    @Override
    public void processResponse(JoinLobbyResponse joinLobbyResponse) {
        lobbySystem.setRooms(joinLobbyResponse.getRooms());
        lobbySystem.setPlayer(joinLobbyResponse.getPlayer());
        screenManager.to(ScreenEnum.LOBBY);
    }

    @Override
    public void processResponse(JoinRoomResponse joinRoomResponse) {
        lobbySystem.setCurrentRoom(joinRoomResponse.getRoom());
        lobbySystem.setPlayer(joinRoomResponse.getPlayer());
        screenManager.to(ScreenEnum.ROOM);
    }

    @Override
    public void processResponse(StartGameResponse startGameResponse) {
        clientSystem.send(new PlayerLoginRequest(lobbySystem.getPlayer()));
        screenManager.to(ScreenEnum.GAME);
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
        if (accountCreationResponse.isSuccessful()) {
            screenManager.to(ScreenEnum.LOGIN);
            Dialog dialog = new Dialog("Exito", screenManager.getAbstractScreen().getSkin());
            dialog.text("Cuenta creada con exito");
            dialog.button("OK");
            dialog.show(screenManager.getAbstractScreen().getStage()); //@todo crear dialogsystem
        }
        else {
            Dialog dialog = new Dialog("Error", screenManager.getAbstractScreen().getSkin());
            dialog.text("Error al crear la cuenta");
            dialog.button("OK");
            dialog.show(screenManager.getAbstractScreen().getStage());
        }
    }

    @Override
    public void processResponse(AccountLoginResponse accountLoginResponse) {
        if (accountLoginResponse.isSuccessful()) {
            // pedimos pasar al lobby del servidor
            clientSystem.send(new JoinLobbyRequest(accountLoginResponse.getUsername()));
        }
        else {
            Dialog dialog = new Dialog("Error", screenManager.getAbstractScreen().getSkin());
            dialog.text("Error al loguearse");
            dialog.button("OK");
            dialog.show(screenManager.getAbstractScreen().getStage());
        }
    }
}
