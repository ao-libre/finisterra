package ar.com.tamborindeguy.objects.types;

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

    public WeaponObj(String name, int grhIndex) {
        super(name, grhIndex);
    }

    @Override
    public Type getType() {
        return Type.WEAPON;
    }
}
