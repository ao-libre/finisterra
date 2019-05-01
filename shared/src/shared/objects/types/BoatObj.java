package shared.objects.types;

import shared.objects.types.common.BonfireObj;

public class BoatObj extends Obj{

    public BoatObj() {}

    public BoatObj(int id, String name, int grhIndex) {
        super(id, name, grhIndex);
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

    private int bodyIndex;
    private int minDef, maxDef;
    private int minHit, maxHit;
    @Override
    public Type getType() {
        return Type.BOAT;
    }
}
