package ar.com.tamborindeguy.objects.types;

public class MineralObj extends Obj {
    private int ingotIndex;

    public MineralObj(String name, int grhIndex) {
        super(name, grhIndex);
    }

    @Override
    public Type getType() {
        return Type.METAL;
    }

    public int getIngotIndex() {
        return ingotIndex;
    }

    public void setIngotIndex(int ingotIndex) {
        this.ingotIndex = ingotIndex;
    }
}
