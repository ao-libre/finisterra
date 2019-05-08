package entity.world;

import com.artemis.Component;
import com.artemis.annotations.DelayedComponentRemoval;

@DelayedComponentRemoval
public class Dialog extends Component {

    public static float DEFAULT_TIME = 20;
    private static float DEFAULT_ALPHA = 1;

    public Kind kind = Kind.MESSAGE;
    public String text;
    public float time = DEFAULT_TIME;
    public float alpha = DEFAULT_ALPHA;

    public Dialog() {
    }

    public Dialog(String text) {
        this.text = text;
        this.time = time;
        this.alpha = alpha;
    }

    public Dialog(String text, Kind kind) {
        this(text);
        this.kind = kind;
    }

    public enum Kind {
        MAGIC_WORDS,
        MESSAGE,
        OTHER
    }
}
