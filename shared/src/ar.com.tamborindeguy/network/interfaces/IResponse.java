package ar.com.tamborindeguy.network.interfaces;

import com.esotericsoftware.kryonet.Connection;

public interface IResponse {
    void accept(IResponseProcessor processor);
}
