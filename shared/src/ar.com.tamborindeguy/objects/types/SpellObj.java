package ar.com.tamborindeguy.objects.types;

public class SpellObj extends Obj {

    private int spellIndex;

    public SpellObj(String name, int grhIndex) {
        super(name, grhIndex);
    }

    @Override
    public Type getType() {
        return Type.SPELL;
    }

    public int getSpellIndex() {
        return spellIndex;
    }

    public void setSpellIndex(int spellIndex) {
        this.spellIndex = spellIndex;
    }
}
