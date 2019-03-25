package shared.network.interfaces;

import shared.network.combat.AttackResponse;
import shared.network.login.LoginFailed;
import shared.network.login.LoginOK;
import shared.network.movement.MovementResponse;

public interface IResponseProcessor {
    void processResponse(LoginOK response);
    void processResponse(LoginFailed response);
    void processResponse(MovementResponse movementResponse);
    void processResponse(AttackResponse attackResponse);
}
