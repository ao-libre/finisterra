package ar.com.tamborindeguy.objects.types;

public class DrinkObj extends Obj {

    private int min;

    public DrinkObj(int id, String name, int grhIndex) {
        super(id, name, grhIndex);
    }

    @Override
    public Type getType() {
        return Type.DRINK;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }
}
