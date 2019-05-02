package entity.world;

import com.artemis.Component;
import com.artemis.annotations.DelayedComponentRemoval;

import java.io.Serializable;

@DelayedComponentRemoval
public class CombatMessage extends Component implements Serializable {

    public static float DEFAULT_TIME = 1.3f;
    public static float DEFAULT_ALPHA = DEFAULT_TIME;
    public static float DEFAULT_OFFSET = 20;
    public Kind kind;

    public String text;
    public float time = DEFAULT_TIME;
    public float alpha = DEFAULT_ALPHA;
    public float offset = DEFAULT_OFFSET;

    public CombatMessage() {
    }

    public static CombatMessage magic(String text) {
        return new CombatMessage(text, Kind.MAGIC);
    }

    public static CombatMessage stab(String text) {
        return new CombatMessage(text, Kind.STAB);
    }

    public static CombatMessage physic(String text) {
        return new CombatMessage(text, Kind.PHYSICAL);
    }

    public CombatMessage(String text, Kind kind) {
        this.text = text;
        this.kind = kind;
    }

    public enum Kind {
        MAGIC,
        PHYSICAL,
        STAB
    }
}
