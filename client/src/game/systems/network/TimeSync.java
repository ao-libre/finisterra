package game.systems.network;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.utils.TimeUtils;
import shared.network.time.TimeSyncRequest;
import shared.network.time.TimeSyncResponse;

@Wire
public class TimeSync extends BaseSystem {

    private static final int SEND_REQUEST_EVERY_X_IN_SEGS = 60;
    private ClientSystem client;
    private int requestId;
    private long sendTime;

    private long rtt;
    private long timeOffset;

    private float time = 0;

    /**
     * Returns a message to be sent, which should be sent immediately as the send time is tracked.
     */
    private TimeSyncRequest send() {
        TimeSyncRequest request = TimeSyncRequest.getNextRequest();
        requestId = request.requestId;
        sendTime = TimeUtils.millis();
        return request;
    }

    private void sendRequest() {
        client.send(send());
    }

    public void receive(TimeSyncResponse response) {
        long receiveTime = TimeUtils.millis();

        if (response.requestId == requestId) {
            rtt = (receiveTime - sendTime) - (response.sendTime - response.receiveTime);
            timeOffset = ((response.receiveTime - sendTime) + (response.sendTime - receiveTime)) / 2;
        }
    }

    public long getRtt() {
        return rtt;
    }

    public long getTimeOffset() {
        return timeOffset;
    }

    public long getMaxTimeOffsetError() {
        return rtt / 2;
    }

    @Override
    protected void processSystem() {
        float delta = getWorld().getDelta();
        time -= delta;
        if (time < 0) {
            time = SEND_REQUEST_EVERY_X_IN_SEGS;
            sendRequest();
        }
    }
}