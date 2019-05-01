package shared.objects.types;

public class DepositObj extends Obj {
    private int mineralIndex;

    public DepositObj() {}

    public DepositObj(int id, String name, int grhIndex) {
        super(id, name, grhIndex);
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
