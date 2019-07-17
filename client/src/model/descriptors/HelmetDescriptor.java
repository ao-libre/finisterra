package model.descriptors;

public class HelmetDescriptor extends Descriptor {

    public HelmetDescriptor() {
    }

    public HelmetDescriptor(int[] headIndex) {
        super(headIndex);
    }

    @Override
    public String toString() {
        return "Helmet: " + getId();
    }
}
