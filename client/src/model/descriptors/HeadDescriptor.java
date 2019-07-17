package model.descriptors;

public class HeadDescriptor extends Descriptor {

    public HeadDescriptor() {
    }

    public HeadDescriptor(int[] headIndex) {
        super(headIndex);
    }

    public HeadDescriptor(HeadDescriptor other) {
        this(other.indexs);
    }

    @Override
    public String toString() {
        return "Head: " + getId();
    }
}
