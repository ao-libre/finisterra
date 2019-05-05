package shared.objects.types.common;


import shared.objects.types.Obj;
import shared.objects.types.Type;

public class FurnitureObj extends Obj {

    public FurnitureObj() {
    }

    public FurnitureObj(int id, String name, int grhIndex) {
        super(id, name, grhIndex);
    }

    @Override
    public Type getType() {
        return Type.FURNITURE;
    }
}
