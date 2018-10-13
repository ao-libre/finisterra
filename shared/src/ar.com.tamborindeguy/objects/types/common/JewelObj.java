package ar.com.tamborindeguy.objects.types.common;


import ar.com.tamborindeguy.objects.types.Obj;
import ar.com.tamborindeguy.objects.types.Type;

public class JewelObj extends Obj {
    public JewelObj(int id, String name, int grhIndex) {
        super(id, name, grhIndex);
    }

    @Override
    public Type getType() {
        return Type.JEWEL;
    }
}
