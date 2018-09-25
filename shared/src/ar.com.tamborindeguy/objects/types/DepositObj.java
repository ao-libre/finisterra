package ar.com.tamborindeguy.objects.types;

public class DepositObj extends Obj {
    private int mineralIndex;

    public DepositObj(String name, int grhIndex) {
        super(name, grhIndex);
    }

    @Override
    public Type getType() {
        return Type.DEPOSIT;
    }

    public int getMineralIndex() {
        return mineralIndex;
    }

    public void setMineralIndex(int mineralIndex) {
        this.mineralIndex = mineralIndex;
    }
}
