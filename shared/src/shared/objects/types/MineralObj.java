package shared.objects.types;

import org.ini4j.Profile;
import shared.objects.factory.ObjectFactory;

public class MineralObj extends Obj {
    private int ingotIndex;

    public MineralObj() {
    }

    public MineralObj(int id, String name, int grhIndex) {
        super(id, name, grhIndex);
    }

    @Override
    public void fillObject(Profile.Section section) {
        super.fillObject(section);
        ObjectFactory.fill(this, section);
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
