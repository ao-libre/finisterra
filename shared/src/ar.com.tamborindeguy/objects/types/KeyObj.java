package ar.com.tamborindeguy.objects.types;

public class KeyObj extends Obj {

    private int key;

    public KeyObj(int id, String name, int grhIndex) {
        super(id, name, grhIndex);
    }

    @Override
    public Type getType() {
        return Type.KEYS;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }
}
