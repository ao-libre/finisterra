package shared.network.user;

import shared.network.interfaces.IRequest;
import shared.network.interfaces.IRequestProcessor;

public class UserCreateRequest implements IRequest {

    // TODO convert to model
    private String name;
    private int heroId, index;
    private String userAcc;

    public UserCreateRequest() {
    }

    public UserCreateRequest(String name, int heroId, String userAcc, int index) {
        this.name = name;
        this.heroId = heroId;
        this.userAcc = userAcc;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public int getHeroId() {
        return heroId;
    }

    public String getUserAcc() {
        return userAcc;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public void accept(IRequestProcessor processor, int connectionId) {
        processor.processRequest(this, connectionId);
    }
}
