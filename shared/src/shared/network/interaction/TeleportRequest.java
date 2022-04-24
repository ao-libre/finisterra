package shared.network.interaction;

import shared.network.interfaces.IRequest;
import shared.network.interfaces.IRequestProcessor;

public class TeleportRequest implements IRequest {

    private int map;
    private int x;
    private int y;

    public TeleportRequest(){

    };

    public TeleportRequest(int map, int x, int y){
        this.map = map;
        this.x = x;
        this.y = y;
    }

    public int getMap(){return this.map;};

    public int getX(){return this.x;};

    public int getY(){return this.y;};

    @Override
    public void accept(IRequestProcessor processor, int connectionId) {
       processor.processRequest(this, connectionId);
    }
}
