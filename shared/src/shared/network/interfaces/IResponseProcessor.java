package shared.network.interfaces;

import shared.network.combat.AttackResponse;
import shared.network.lobby.CreateRoomResponse;
import shared.network.lobby.JoinLobbyResponse;
import shared.network.lobby.JoinRoomResponse;
import shared.network.lobby.StartGameResponse;
import shared.network.login.LoginFailed;
import shared.network.login.LoginOK;
import shared.network.movement.MovementResponse;

public interface IResponseProcessor {
    void processResponse(LoginOK response);

    void processResponse(LoginFailed response);

    void processResponse(MovementResponse movementResponse);

    void processResponse(AttackResponse attackResponse);

    void processResponse(CreateRoomResponse createRoomResponse);

    void processResponse(JoinLobbyResponse joinLobbyResponse);

    void processResponse(JoinRoomResponse joinRoomResponse);

    void processResponse(StartGameResponse startGameResponse);
}