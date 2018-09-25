package ar.com.tamborindeguy.network.map;

import ar.com.tamborindeguy.network.interfaces.IRequest;
import ar.com.tamborindeguy.network.interfaces.IRequestProcessor;

public class MapRequest implements IRequest {

    private int mapNumber;

    public MapRequest(){}

    public MapRequest(int mapNumber){
        this.mapNumber = mapNumber;
    }

    @Override
    public void accept(IRequestProcessor processor, int connectionId) {
        processor.processRequest(this, connectionId);
    }

    public int getMapNumber() {
        return mapNumber;
    }

    public void setMapNumber(int mapNumber) {
        this.mapNumber = mapNumber;
    }
}
