package ar.com.tamborindeguy.objects.types;

public class KeyObj extends Obj {

    private int key;

    public KeyObj(String name, int grhIndex) {
        super(name, grhIndex);
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
