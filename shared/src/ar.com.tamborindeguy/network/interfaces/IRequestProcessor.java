package ar.com.tamborindeguy.network.interfaces;

import ar.com.tamborindeguy.network.combat.AttackRequest;
import ar.com.tamborindeguy.network.login.LoginRequest;
import ar.com.tamborindeguy.network.movement.MovementRequest;

public interface IRequestProcessor {

    void processRequest(LoginRequest request, int connectionId);
    void processRequest(MovementRequest request, int connectionId);
    void processRequest(AttackRequest attackRequest, int connectionId);
}
