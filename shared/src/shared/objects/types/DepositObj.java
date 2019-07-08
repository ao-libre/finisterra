package shared.objects.types;

import org.ini4j.Profile;
import shared.objects.factory.ObjectFactory;

public class DepositObj extends Obj {
    private int mineralIndex;

    public DepositObj() {
    }

    public DepositObj(int id, String name, int grhIndex) {
        super(id, name, grhIndex);
    }

    @Override
    public void fillObject(Profile.Section section) {
        super.fillObject(section);
        ObjectFactory.fill(this, section);
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
