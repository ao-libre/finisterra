package shared.network.login;

import shared.network.interfaces.IResponse;
import shared.network.interfaces.IResponseProcessor;
@Deprecated
public class LoginOK implements IResponse {

    public int entityId;

    public LoginOK() {}

    public LoginOK(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public void accept(IResponseProcessor processor) {
        processor.processResponse(this);
    }
}
