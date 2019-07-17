package model.descriptors;

public class WeaponDescriptor extends Descriptor {

    public WeaponDescriptor() {
    }

    public WeaponDescriptor(int[] weaponIndex) {
        super(weaponIndex);
    }

    @Override
    public String toString() {
        return "Weapon: " + getId();
    }
}
