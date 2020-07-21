package shared.network.user;

import shared.network.interfaces.IRequest;
import shared.network.interfaces.IRequestProcessor;

public class UserContinueRequest implements IRequest {

    // TODO convert to model
    private String name;

    public UserContinueRequest() {
    }

    public UserContinueRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    @Override
    public void accept(IRequestProcessor processor, int connectionId) {
        processor.processRequest(this, connectionId);
    }


}
