package shared.objects.types.common;


import shared.objects.types.Obj;
import shared.objects.types.Type;

public class BookObj extends Obj {

    public BookObj() {
    }

    public BookObj(int id, String name, int grhIndex) {
        super(id, name, grhIndex);
    }

    @Override
    public Type getType() {
        return Type.BOOK;
    }
}
