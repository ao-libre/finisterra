package shared.objects.types;

import org.ini4j.Profile;
import shared.objects.factory.ObjectFactory;

public class ArrowObj extends ObjWithClasses {

    private int minHit, maxHit;

    public ArrowObj(int id, String name, int grhIndex) {
        super(id, name, grhIndex);
    }

    public ArrowObj() {
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
        return Type.ARROW;
    }

    @Override
    public void fillObject(Profile.Section section) {
        super.fillObject(section);
        ObjectFactory.fill(this, section);
    }
}
