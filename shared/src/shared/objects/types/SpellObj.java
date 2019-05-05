package shared.objects.types;

public class SpellObj extends Obj {

    private int spellIndex;

    public SpellObj() {
    }

    public SpellObj(int id, String name, int grhIndex) {
        super(id, name, grhIndex);
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
