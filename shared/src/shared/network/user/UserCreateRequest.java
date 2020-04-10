package shared.network.user;

import shared.network.interfaces.IRequest;
import shared.network.interfaces.IRequestProcessor;

public class UserCreateRequest implements IRequest {

    // TODO convert to model
    private String name;
    private int heroId;

    public UserCreateRequest() {}

    public UserCreateRequest(String name, int heroId) {
        this.name = name;
        this.heroId = heroId;
    }

    public String getName() {
        return name;
    }

    public int getHeroId() {
        return heroId;
    }

    @Override
    public void accept(IRequestProcessor processor, int connectionId) {
        processor.processRequest(this, connectionId);
    }
}
