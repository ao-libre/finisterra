package shared.objects.types;

import org.ini4j.Profile;
import shared.objects.factory.ObjectFactory;

public class ArmorObj extends ObjWithClasses {

    private int bodyNumber;
    private int minDef, maxDef;
    private boolean women;
    private boolean dwarf;

    public ArmorObj() {
    }

    public ArmorObj(int id, String name, int grhIndex) {
        super(id, name, grhIndex);
    }

    @Override
    public Type getType() {
        return Type.ARMOR;
    }

    @Override
    public void fillObject(Profile.Section section) {
        super.fillObject(section);
        ObjectFactory.fill(this, section);
    }

    public int getBodyNumber() {
        return bodyNumber;
    }

    public void setBodyNumber(int bodyNumber) {
        this.bodyNumber = bodyNumber;
    }

    public int getMinDef() {
        return minDef;
    }

    public void setMinDef(int minDef) {
        this.minDef = minDef;
    }

    public int getMaxDef() {
        return maxDef;
    }

    public void setMaxDef(int maxDef) {
        this.maxDef = maxDef;
    }

    public boolean isWomen() {
        return women;
    }

    public void setWomen(boolean women) {
        this.women = women;
    }

    public boolean isDwarf() {
        return dwarf;
    }

    public void setDwarf(boolean dwarf) {
        this.dwarf = dwarf;
    }
}
