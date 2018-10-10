package entity;

import com.artemis.Component;

import java.io.Serializable;

public class CombatMessage extends Component implements Serializable {

    public static float DEFAULT_TIME = 2;
    public static float DEFAULT_ALPHA = DEFAULT_TIME;
    public static float DEFAULT_OFFSET = 10;

    public String text;
    public float time = DEFAULT_TIME;
    public float alpha = DEFAULT_ALPHA;
    public float offset = DEFAULT_OFFSET;

    public CombatMessage() {
    }

    public CombatMessage(String text) {
        this.text = text;
    }
}
