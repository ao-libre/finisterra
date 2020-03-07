package shared.network.interfaces;

import shared.network.account.AccountLoginResponse;
import shared.network.lobby.CreateRoomResponse;
import shared.network.lobby.JoinLobbyResponse;
import shared.network.lobby.JoinRoomResponse;
import shared.network.lobby.StartGameResponse;
import shared.network.movement.MovementResponse;
import shared.network.time.TimeSyncResponse;

public interface IResponseProcessor {

    void processResponse(MovementResponse movementResponse);

    void processResponse(CreateRoomResponse createRoomResponse);

    void processResponse(JoinLobbyResponse joinLobbyResponse);

    void processResponse(JoinRoomResponse joinRoomResponse);

    void processResponse(StartGameResponse startGameResponse);

    void processResponse(TimeSyncResponse timeSyncResponse);

    void processResponse(AccountLoginResponse accountLoginResponse);
}
