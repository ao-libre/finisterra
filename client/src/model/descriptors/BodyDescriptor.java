package model.descriptors;

public class BodyDescriptor extends Descriptor {

    public int headOffsetX;
    public int headOffsetY;

    public BodyDescriptor() {
    }

    public BodyDescriptor(int[] grhIndex, int headOffsetX, int headOffsetY) {
        super(grhIndex);
        this.headOffsetX = headOffsetX;
        this.headOffsetY = headOffsetY;
    }

    public int getHeadOffsetX() {
        return headOffsetX;
    }

    public void setHeadOffsetX(int headOffsetX) {
        this.headOffsetX = headOffsetX;
    }

    public int getHeadOffsetY() {
        return headOffsetY;
    }

    public void setHeadOffsetY(int headOffsetY) {
        this.headOffsetY = headOffsetY;
    }

    @Override
    public String toString() {
        return "Body: " + getId();
    }
}
