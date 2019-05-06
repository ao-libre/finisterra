package shared.objects.types.common;


import shared.objects.types.Obj;
import shared.objects.types.Type;

public class BonfireObj extends Obj {

    public BonfireObj() {
    }

    public BonfireObj(int id, String name, int grhIndex) {
        super(id, name, grhIndex);
    }

    @Override
    public Type getType() {
        return Type.BONFIRE;
    }
}
