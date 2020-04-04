package shared.network.combat;

import com.badlogic.gdx.utils.TimeUtils;
import component.position.WorldPos;
import shared.model.Spell;
import shared.network.interfaces.IRequest;
import shared.network.interfaces.IRequestProcessor;

public class SpellCastRequest implements IRequest {
    private Spell spell;
    private WorldPos worldPos;
    private long timestamp;

    public SpellCastRequest() {
    }

    public SpellCastRequest(Spell spell, WorldPos worldPos, long timestamp) {
        this.spell = spell;
        this.worldPos = worldPos;
        this.timestamp = TimeUtils.millis() + timestamp;
    }

    public Spell getSpell() {
        return spell;
    }

    public WorldPos getWorldPos() {
        return worldPos;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public void accept(IRequestProcessor processor, int connectionId) {
        processor.processRequest(this, connectionId);
    }
}
