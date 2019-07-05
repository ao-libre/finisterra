package entity.character.info;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

import java.io.Serializable;

@PooledWeaver
public class SpellBook extends Component implements Serializable {

    public final static int SIZE = 25;
    public Integer[] spells = new Integer[SIZE];

    public SpellBook() {
    }

    public SpellBook(Integer[] spells) {
        this.spells = spells;
    }

    public Integer[] getSpells() {
        return spells;
    }

    public void setSpells(Integer[] spells) {
        this.spells = spells;
    }

    public void set(int i, Integer spellId) {
        spells[i] = spellId;
    }

}
