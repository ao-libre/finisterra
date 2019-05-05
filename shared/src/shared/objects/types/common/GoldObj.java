package shared.objects.types.common;


import shared.objects.types.Obj;
import shared.objects.types.Type;

public class GoldObj extends Obj {

    public GoldObj() {
    }

    public GoldObj(int id, String name, int grhIndex) {
        super(id, name, grhIndex);
    }

    @Override
    public Type getType() {
        return Type.GOLD;
    }
}
