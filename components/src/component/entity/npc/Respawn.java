package component.entity.npc;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

import java.io.Serializable;

@PooledWeaver
public class Respawn extends Component implements Serializable {

    private float time;
    private int npcId;
    private OriginPos pos;

    public Respawn() {
    }

    public Respawn(float time, int npcId, OriginPos pos) {
        this.time = time;
        this.npcId = npcId;
        this.pos = pos;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public int getNpcId() {
        return npcId;
    }

    public void setNpcId(int npcId) {
        this.npcId = npcId;
    }

    public OriginPos getPos() {
        return pos;
    }

    public void setPos(OriginPos pos) {
        this.pos = pos;
    }
}
