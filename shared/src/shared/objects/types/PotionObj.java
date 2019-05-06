package shared.objects.types;

import org.ini4j.Profile;
import shared.objects.factory.ObjectFactory;

public class PotionObj extends Obj {

    private PotionKind kind;
    private int min, max;
    private int effecTime;

    public PotionObj() {
    }

    public PotionObj(int id, String name, int grhIndex) {
        super(id, name, grhIndex);
    }

    public PotionKind getKind() {
        return kind;
    }

    public void setKind(int kind) {
        if (kind > PotionKind.values().length) {
            return;
        }
        this.kind = PotionKind.values()[kind - 1];
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getEffecTime() {
        return effecTime;
    }

    public void setEffecTime(int effecTime) {
        this.effecTime = effecTime;
    }

    @Override
    public Type getType() {
        return Type.POTION;
    }

    @Override
    public void fillObject(Profile.Section section) {
        super.fillObject(section);
        ObjectFactory.fill(this, section);
    }
}


