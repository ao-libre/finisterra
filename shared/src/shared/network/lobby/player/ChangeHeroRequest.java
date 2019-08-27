package shared.network.lobby.player;

import shared.interfaces.Hero;
import shared.network.interfaces.IRequest;
import shared.network.interfaces.IRequestProcessor;

public class ChangeHeroRequest implements IRequest {

    private Hero hero;

    public ChangeHeroRequest() { }

    public ChangeHeroRequest(Hero hero) {
        this.hero = hero;
    }

    public Hero getHero() {
        return hero;
    }

    @Override
    public void accept(IRequestProcessor processor, int connectionId) {
        processor.processRequest(this, connectionId);
    }
}
