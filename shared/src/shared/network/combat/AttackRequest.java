package shared.network.combat;

import com.badlogic.gdx.utils.TimeUtils;
import component.position.WorldPos;
import shared.model.AttackType;
import shared.network.interfaces.IRequest;
import shared.network.interfaces.IRequestProcessor;

public class AttackRequest implements IRequest {

    private AttackType type;
    private WorldPos worldPos;
    private long timestamp;

    public AttackRequest() {
    }

    public AttackRequest(AttackType type, WorldPos worldPos, long timestamp) {
        this.type = type;
        this.worldPos = worldPos;
        this.timestamp = TimeUtils.millis() + timestamp;
    }

    public AttackRequest(AttackType type) {
        this.type = type;
    }

    public WorldPos getWorldPos() {
        return worldPos;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public AttackType type() {
        return type;
    }

    @Override
    public void accept(IRequestProcessor processor, int connectionId) {
        processor.processRequest(this, connectionId);
    }
}
