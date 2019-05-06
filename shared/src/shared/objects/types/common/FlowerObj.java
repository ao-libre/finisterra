package shared.objects.types.common;


import shared.objects.types.Obj;
import shared.objects.types.Type;

public class FlowerObj extends Obj {

    public FlowerObj() {
    }

    public FlowerObj(int id, String name, int grhIndex) {
        super(id, name, grhIndex);
    }

    @Override
    public Type getType() {
        return Type.FLOWER;
    }
}
