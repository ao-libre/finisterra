package entity;

import com.artemis.Component;

import java.io.Serializable;

public class CombatMessage extends Component implements Serializable {
    public String text;
    public float time = Dialog.DEFAULT_TIME;
    public float alpha = Dialog.DEFAULT_ALPHA;

    public CombatMessage() {
    }

    public CombatMessage(String text) {
        this.text = text;
    }
}
