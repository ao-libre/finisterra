package ar.com.tamborindeguy.model.descriptors;

public class Descriptor implements IDescriptor {

    public int id;
    protected int[] indexs;

    public Descriptor() {
    }

    public Descriptor(int[] grhIndex) {
        this.indexs = grhIndex;
    }

    public int[] getIndexs() {
        return indexs;
    }

    public void setIndexs(int[] indexs) {
        this.indexs = indexs;
    }

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
