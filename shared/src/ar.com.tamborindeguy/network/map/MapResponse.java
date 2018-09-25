package ar.com.tamborindeguy.network.map;

import ar.com.tamborindeguy.model.map.Map;
import ar.com.tamborindeguy.network.interfaces.IResponse;
import ar.com.tamborindeguy.network.interfaces.IResponseProcessor;

public class MapResponse implements IResponse {

    private int mapNumber;
    private Map map;

    public MapResponse() {
    }

    public MapResponse(Map map, int number) {
        this.map = map;
        this.mapNumber = number;
    }

    @Override
    public void accept(IResponseProcessor processor) {
        processor.processResponse(this);
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public int getMapNumber() {
        return mapNumber;
    }

    public void setMapNumber(int mapNumber) {
        this.mapNumber = mapNumber;
    }
}
