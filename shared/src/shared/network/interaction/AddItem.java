package shared.network.interaction;

import shared.network.interfaces.INotification;
import shared.network.interfaces.INotificationProcessor;
import shared.objects.types.Obj;

public class AddItem implements INotification {

    private int playerId;
    private Obj obj;

    public AddItem(){

    }
    public AddItem(int playerId,  Obj obj) {
        this.playerId = playerId;
        this.obj = obj;
    }


    @Override
    public void accept(INotificationProcessor processor) {
        processor.processNotification(this);
    }

    public Obj getObj() {
        return obj;
    }

    public int getPlayerId() {
        return playerId;
    }
}
