package shared.network.lobby.player;

import shared.interfaces.Hero;
import shared.network.interfaces.INotification;
import shared.network.interfaces.INotificationProcessor;

public class ChangeHeroNotification implements INotification {

    private Hero hero;

    public ChangeHeroNotification() {
    }

    public ChangeHeroNotification(Hero hero) {
        this.hero = hero;
    }

    public Hero getHero() {
        return hero;
    }

    @Override
    public void accept(INotificationProcessor processor) {
        processor.processNotification(this);
    }
}
