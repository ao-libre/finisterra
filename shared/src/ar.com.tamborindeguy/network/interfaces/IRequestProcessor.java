package ar.com.tamborindeguy.network.interfaces;

import ar.com.tamborindeguy.network.map.MapRequest;
import ar.com.tamborindeguy.network.login.LoginRequest;
import ar.com.tamborindeguy.network.movement.MovementRequest;

public interface IRequestProcessor {

    void processRequest(LoginRequest request, int connectionId);

    void processRequest(MovementRequest request, int connectionId);

    void processRequest(MapRequest mapRequest, int connectionId);
}
