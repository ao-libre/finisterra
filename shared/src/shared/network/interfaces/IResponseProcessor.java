package shared.network.interfaces;

import shared.network.account.AccountCreationResponse;
import shared.network.account.AccountLoginResponse;
import shared.network.movement.MovementResponse;
import shared.network.time.TimeSyncResponse;
import shared.network.user.UserCreateResponse;
import shared.network.user.UserLoginResponse;
import shared.network.user.UserLogoutResponse;

public interface IResponseProcessor {

    void processResponse(MovementResponse movementResponse);

    void processResponse(TimeSyncResponse timeSyncResponse);

    void processResponse(AccountCreationResponse accountCreationResponse);

    void processResponse(AccountLoginResponse accountLoginResponse);

    void processResponse(UserCreateResponse userCreateResponse);

    void processResponse(UserLoginResponse userLoginResponse);

    void processResponse(UserLogoutResponse userLogoutResponse);
}
