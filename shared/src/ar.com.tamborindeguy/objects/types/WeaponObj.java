package ar.com.tamborindeguy.objects.types;

import ar.com.tamborindeguy.objects.factory.ObjectFactory;
import org.ini4j.Profile;

public class WeaponObj extends ObjWithClasses {

    private int animationId;
    private int dwarfAnimationId;
    private int minHit, maxHit;

    public int getAnimationId() {
        return animationId;
    }

    public void setAnimationId(int animationId) {
        this.animationId = animationId;
    }

    public int getDwarfAnimationId() {
        return dwarfAnimationId;
    }

    public void setDwarfAnimationId(int dwarfAnimationId) {
        this.dwarfAnimationId = dwarfAnimationId;
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

    public WeaponObj(int id, String name, int grhIndex) {
        super(id, name, grhIndex);
    }

    @Override
    public Type getType() {
        return Type.WEAPON;
    }

    @Override
    public void fillObject(Profile.Section section) {
        super.fillObject(section);
        ObjectFactory.fill(this, section);
    }
}
