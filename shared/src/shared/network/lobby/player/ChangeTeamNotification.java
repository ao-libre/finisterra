package shared.network.lobby.player;

import shared.model.lobby.Team;
import shared.network.interfaces.INotification;
import shared.network.interfaces.INotificationProcessor;

public class ChangeTeamNotification implements INotification {

    private Team team;

    public ChangeTeamNotification() {
    }

    public ChangeTeamNotification(Team team) {
        this.team = team;
    }

    public Team getTeam() {
        return team;
    }

    @Override
    public void accept(INotificationProcessor processor) {
        processor.processNotification(this);
    }
}
