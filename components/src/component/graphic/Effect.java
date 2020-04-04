package design.graphic;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;
import component.entity.Index;

import java.io.Serializable;

@PooledWeaver
public class Effect extends Component implements Serializable, Index {

    public static final int NO_REF = -1;

    public int entityReference = NO_REF; //TODO change reference when arrive to client
    public int effectId;
    public int loops;
    public Type type;

    public Effect() {
    }

    @Override
    public int getIndex() {
        return effectId;
    }

    public enum Type {
        PARTICLE,
        FX
    }

}
