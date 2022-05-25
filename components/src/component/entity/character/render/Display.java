package component.entity.character.render;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import java.io.Serializable;
import java.util.Arrays;

@PooledWeaver
public class Display extends Component implements Serializable {

    public Table display;
    private ProgressBar hp;
    private ProgressBar mana;

    public Display() {
        display = new Table();
        display.setRound(false);
    }

    public Table getDisplay() {
        return display;
    }

    public void addLabel(Label label, float prefWidth) {
        label.setWrap(true);
        label.setAlignment(Align.center);
        display.row();
        display.add(label).width(Math.min(prefWidth + 20, 200));

    }

    public void addHp(ProgressBar hp) {
        this.hp = hp;
        display.row();
        display.add(hp).center().width(64).height(10);
    }

    public void addMana(ProgressBar mana) {
        this.mana = mana;
        display.add(mana).center().width(64).height(10);
    }

    public void setHp(int value, int max) {
        if (hasHp()) {
            if (hp.getMaxValue() != max) {
                hp.setRange(0, max);
            }
            if (hp.getValue() != value) {
                hp.setValue(value);
            }
        }
    }

    public void setMana(int value, int max) {
        if (hasMana()) {
            if (mana.getMaxValue() != max) {
                mana.setRange(0, max);
            }
            if (mana.getValue() != value) {
                mana.setValue(value);
            }
        }
    }

    public boolean hasMana() {
        return mana != null;
    }

    public boolean hasHp() {
        return hp != null;
    }

    public Label getLabel() {
        return Arrays.stream(display.getChildren().items).filter(Label.class::isInstance).map(Label.class::cast).findFirst().get();
    }
}
