package ar.com.tamborindeguy.objects.types;

public class MineralObj extends Obj {
    private int ingotIndex;

    public MineralObj(int id, String name, int grhIndex) {
        super(id, name, grhIndex);
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
