package ar.com.tamborindeguy.objects.types.common;

import ar.com.tamborindeguy.objects.types.Obj;
import ar.com.tamborindeguy.objects.types.Type;

public class AnvilObj extends Obj {
    public AnvilObj(String name, int grhIndex) {
        super(name, grhIndex);
    }

    @Override
    public Type getType() {
        return Type.ANVIL;
    }
}
