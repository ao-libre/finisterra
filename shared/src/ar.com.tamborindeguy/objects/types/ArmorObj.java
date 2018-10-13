package ar.com.tamborindeguy.objects.types;

import ar.com.tamborindeguy.objects.factory.ObjectFactory;
import org.ini4j.Profile;

public class ArmorObj extends ObjWithClasses {

    private int bodyNumber;
    private int minDef, maxDef;
    private boolean women;
    private boolean dwarf;

    public ArmorObj(int id, String name, int grhIndex) {
        super(id, name, grhIndex);
    }

    @Override
    public Type getType() {
        return Type.ARMOR;
    }

    public void setDwarf(boolean dwarf) {
        this.dwarf = dwarf;
    }

    public void setWomen(boolean women) {
        this.women = women;
    }

    public void setMaxDef(int maxDef) {
        this.maxDef = maxDef;
    }

    public void setMinDef(int minDef) {
        this.minDef = minDef;
    }

    public void setBodyNumber(int bodyNumber) {
        this.bodyNumber = bodyNumber;
    }

    @Override
    public void fillObject(Profile.Section section) {
        super.fillObject(section);
        ObjectFactory.fill(this, section);
    }

    public int getBodyNumber() {
        return bodyNumber;
    }

    public int getMinDef() {
        return minDef;
    }

    public int getMaxDef() {
        return maxDef;
    }

    public boolean isWomen() {
        return women;
    }

    public boolean isDwarf() {
        return dwarf;
    }
}
