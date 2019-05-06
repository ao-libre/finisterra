package shared.network.time;

import shared.network.interfaces.IResponse;
import shared.network.interfaces.IResponseProcessor;

public class TimeSyncResponse implements IResponse {
    public int requestId;
    public long receiveTime;
    public long sendTime;

    @Override
    public void accept(IResponseProcessor processor) {
        processor.processResponse(this);
    }
}
