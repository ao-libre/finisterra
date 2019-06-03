package shared.objects.types;

import org.ini4j.Profile;
import shared.objects.factory.ObjectFactory;

public class SpellObj extends Obj {

    private int spellIndex;

    public SpellObj() {
    }

    public SpellObj(int id, String name, int grhIndex) {
        super(id, name, grhIndex);
    }

    @Override
    public void fillObject(Profile.Section section) {
        super.fillObject(section);
        ObjectFactory.fill(this, section);
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
