package model.descriptors;

public class ShieldDescriptor extends Descriptor {

    public ShieldDescriptor() {
    }

    public ShieldDescriptor(int[] shieldIndex) {
        super(shieldIndex);
    }

    @Override
    public String toString() {
        return "Shield: " + getId();
    }
}
