package ar.com.tamborindeguy.network.interfaces;

import ar.com.tamborindeguy.network.combat.AttackRequest;
import ar.com.tamborindeguy.network.interaction.MeditateRequest;
import ar.com.tamborindeguy.network.interaction.TalkRequest;
import ar.com.tamborindeguy.network.inventory.ItemActionRequest;
import ar.com.tamborindeguy.network.login.LoginRequest;
import ar.com.tamborindeguy.network.movement.MovementRequest;

public interface IRequestProcessor {

    void processRequest(LoginRequest request, int connectionId);
    void processRequest(MovementRequest request, int connectionId);
    void processRequest(AttackRequest attackRequest, int connectionId);
    void processRequest(ItemActionRequest itemAction, int connectionId);
    void processRequest(MeditateRequest meditateRequest, int connectionId);
    void processRequest(TalkRequest talkRequest, int connectionId);
}
