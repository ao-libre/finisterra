package ar.com.tamborindeguy.objects.types;

public class Food extends Obj {
    private int min;

    public Food(String name, int grhIndex) {
        super(name, grhIndex);
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
