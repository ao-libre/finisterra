package ar.com.tamborindeguy.objects.types.common;


import ar.com.tamborindeguy.objects.types.Obj;
import ar.com.tamborindeguy.objects.types.Type;

public class GoldObj extends Obj {
    public GoldObj(int id, String name, int grhIndex) {
        super(id, name, grhIndex);
    }

    @Override
    public Type getType() {
        return Type.GOLD;
    }
}
