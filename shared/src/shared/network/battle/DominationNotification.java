package shared.network.battle;

import shared.model.lobby.Team;
import shared.network.interfaces.INotification;
import shared.network.interfaces.INotificationProcessor;

public class DominationNotification implements INotification {

    public static final float TIME_TO_DOMINATE = 45;

    private Team dominating;

    public DominationNotification(){}
    public DominationNotification(Team dominating){
        this.dominating = dominating;
    }

    public Team getDominating() {
        return dominating;
    }

    public void setDominating(Team dominating) {
        this.dominating = dominating;
    }

    @Override
    public void accept(INotificationProcessor processor) {
        processor.processNotification(this);
    }
}
