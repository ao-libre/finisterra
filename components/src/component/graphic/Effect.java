package component.graphic;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;
import component.entity.Index;

import java.io.Serializable;

@PooledWeaver
public class Effect extends Component implements Serializable, Index {

    public static final int LOOP_INFINITE = -1;

    public int effectId;
    public int loops;
    public Type type;

    public Effect() {
    }

    @Override
    public int getIndex() {
        return effectId;
    }

    public int getEffectId() {
        return effectId;
    }

    public int getLoops() {
        return loops;
    }

    public Type getType() {
        return type;
    }

    public void setEffectId(int effectId) {
        this.effectId = effectId;
    }

    public void setLoops(int loops) {
        this.loops = loops;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public enum Type {
        PARTICLE,
        FX
    }

}
