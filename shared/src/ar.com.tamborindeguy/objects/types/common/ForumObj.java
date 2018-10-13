package ar.com.tamborindeguy.objects.types.common;


import ar.com.tamborindeguy.objects.types.Obj;
import ar.com.tamborindeguy.objects.types.Type;

public class ForumObj extends Obj {

    public ForumObj(int id, String name, int grhIndex) {
        super(id, name, grhIndex);
    }

    @Override
    public Type getType() {
        return Type.FORUM;
    }
}
