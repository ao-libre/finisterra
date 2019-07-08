package shared.objects.types;

import org.ini4j.Profile;
import shared.objects.factory.ObjectFactory;

public class BoatObj extends Obj {

    private int bodyIndex;
    private int minDef, maxDef;
    private int minHit, maxHit;

    public BoatObj() {
    }

    public BoatObj(int id, String name, int grhIndex) {
        super(id, name, grhIndex);
    }

    @Override
    public void fillObject(Profile.Section section) {
        super.fillObject(section);
        ObjectFactory.fill(this, section);
    }

    public int getBodyIndex() {
        return bodyIndex;
    }

    public void setBodyIndex(int bodyIndex) {
        this.bodyIndex = bodyIndex;
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

    public int getMaxHit() {
        return maxHit;
    }

    public void setMaxHit(int maxHit) {
        this.maxHit = maxHit;
    }

    @Override
    public Type getType() {
        return Type.BOAT;
    }
}
