package ar.com.tamborindeguy.network.interfaces;

import ar.com.tamborindeguy.network.combat.AttackResponse;
import ar.com.tamborindeguy.network.login.LoginFailed;
import ar.com.tamborindeguy.network.login.LoginOK;
import ar.com.tamborindeguy.network.movement.MovementResponse;

public interface IResponseProcessor {
    void processResponse(LoginOK response);
    void processResponse(LoginFailed response);
    void processResponse(MovementResponse movementResponse);
    void processResponse(AttackResponse attackResponse);
}
