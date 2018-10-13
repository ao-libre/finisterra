package ar.com.tamborindeguy.objects.types;

public class Food extends Obj {
    private int min;

    public Food(int id, String name, int grhIndex) {
        super(id, name, grhIndex);
    }

    @Override
    public Type getType() {
        return Type.FOOD;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }
}
