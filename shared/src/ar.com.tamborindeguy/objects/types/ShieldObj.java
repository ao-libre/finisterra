package ar.com.tamborindeguy.objects.types;

import ar.com.tamborindeguy.objects.factory.ObjectFactory;
import org.ini4j.Profile;

public class ShieldObj extends ObjWithClasses {

    private int bodyNumber;
    private int animationId;
    private int minDef, maxDef;

    public int getAnimationId() {
        return animationId;
    }

    public void setAnimationId(int animationId) {
        this.animationId = animationId;
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

    public int getBodyNumber() {
        return bodyNumber;
    }

    public void setBodyNumber(int bodyNumber) {
        this.bodyNumber = bodyNumber;
    }

    public ShieldObj(String name, int grhIndex) {
        super(name, grhIndex);
    }

    @Override
    public Type getType() {
        return Type.SHIELD;
    }

    @Override
    public void fillObject(Profile.Section section) {
        super.fillObject(section);
        ObjectFactory.fill(this, section);
    }
}
