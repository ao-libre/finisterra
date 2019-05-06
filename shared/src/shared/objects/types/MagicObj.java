package shared.objects.types;

import org.ini4j.Profile;
import shared.objects.factory.ObjectFactory;

public class MagicObj extends ObjWithClasses {
    private int minHit, maxHit;
    private int minDef, maxDef;

    public MagicObj() {
    }

    public MagicObj(int id, String name, int grhIndex) {
        super(id, name, grhIndex);
    }

    public int getMaxHit() {
        return maxHit;
    }

    public void setMaxHit(int maxHit) {
        this.maxHit = maxHit;
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

    public int getMinHit() {
        return minHit;
    }

    public void setMinHit(int minHit) {
        this.minHit = minHit;
    }

    @Override
    public Type getType() {
        return Type.RING;
    }

    @Override
    public void fillObject(Profile.Section section) {
        super.fillObject(section);
        ObjectFactory.fill(this, section);
    }
}
