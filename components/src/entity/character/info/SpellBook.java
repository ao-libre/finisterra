package entity.character.info;

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


    public void addSpell(int spellId){

        boolean spellPresent = false;
        System.out.println (spells.length);
        for(int i= 0; i < spells.length; i++ ) {
                if (spells[i] == spellId) {
                    spellPresent = true;
                 }
        }
        if (spellPresent == false){
            if (spells.length < SIZE){
                int spellLenght = (spells.length + 1);
                Integer[] spells1 = new Integer[spellLenght];
                for (int i = 0 ; i < spells.length ; i++){
                    spells1[i] = spells[i];
                }
                spells1[spellLenght-1] = spellId;
                msj = "hechiso agregado";
                setSpells ( spells1 );
                for(int i= 0; i < spells.length; i++ ){
                }
            }
        }else {
            msj = "ya conoces el hechiso o tu libro de echisos esta lleno";
        }

    }

    public String getMsj() {
        return msj;
    }
}
