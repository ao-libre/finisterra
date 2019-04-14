package entity.character.info;

import com.artemis.Component;

import java.io.Serializable;

public class SpellBook extends Component implements Serializable {

    public Integer[] spells;

    public SpellBook() {}

    public SpellBook(Integer[] spells) {
        this.spells = spells;
    }

}
