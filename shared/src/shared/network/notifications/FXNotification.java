package shared.network.notifications;

import shared.network.interfaces.INotification;
import shared.network.interfaces.INotificationProcessor;

public class FXNotification implements INotification {
    private int target;
    private int fxGrh;

    public FXNotification() {
    }

    public FXNotification(int target, int fxGrh) {
        this.target = target;
        this.fxGrh = fxGrh;
    }

    public int getFxGrh() {
        return fxGrh;
    }

    public int getTarget() {
        return target;
    }

    @Override
    public void accept(INotificationProcessor processor) {
        processor.processNotification(this);
    }
}
