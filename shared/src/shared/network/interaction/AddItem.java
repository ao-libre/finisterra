package shared.network.interaction;

import shared.network.interfaces.INotification;
import shared.network.interfaces.INotificationProcessor;
import shared.objects.types.Obj;

public class AddItem implements INotification {

    private int playerId;
    private int count;
    private int objID;

    public AddItem(){

    }
    public AddItem(int playerId,  int objID, int count) {
        this.playerId = playerId;
        this.objID = objID;
        this.count = count;
    }

    @Override
    public void accept(INotificationProcessor processor) {
        processor.processNotification(this);
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getCount() {
        return count;
    }

    public int getObjID() {
        return objID;
    }
}
