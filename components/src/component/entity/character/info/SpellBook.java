package component.entity.character.info;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

import java.io.Serializable;

@PooledWeaver
public class SpellBook extends Component implements Serializable {

    public final static int SIZE = 25;
    public Integer[] spells = new Integer[SIZE];
    private String msj = "";

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


    public void addSpell(int spellId) {

        boolean spellPresent = false;
        System.out.println(spells.length);
        for (Integer spell : spells) {
            if (spell == spellId) {
                spellPresent = true;
                break;
            }
        }
        if (!spellPresent) {
            if (spells.length < SIZE) {
                int spellLength = (spells.length + 1);
                Integer[] spells1 = new Integer[spellLength];
                System.arraycopy(spells, 0, spells1, 0, spells.length);
                spells1[spellLength - 1] = spellId;
                msj = "hechiso agregado";
                setSpells(spells1);
            }
        } else {
            msj = "ya conoces el hechiso o tu libro de echisos esta lleno";
        }

    }

    public String getMsj() {
        return msj;
    }
}
