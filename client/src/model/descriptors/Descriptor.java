package model.descriptors;

import model.ID;

public class Descriptor implements IDescriptor, ID {

    public int id;
    protected int[] indexs;

    public Descriptor() {
        indexs = new int[4];
    }

    public Descriptor(int[] grhIndex) {
        this.indexs = grhIndex;
    }

    @Override
    public int[] getIndexs() {
        return indexs;
    }

    public void setIndexs(int[] indexs) {
        this.indexs = indexs;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGraphic(int index) {
        return this.indexs[index];
    }
}
