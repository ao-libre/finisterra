package shared.objects.types;

import org.ini4j.Profile;
import shared.objects.factory.ObjectFactory;

public class Food extends Obj {
    private int min;

    public Food() {
        super();
    }

    public Food(int id, String name, int grhIndex) {
        super(id, name, grhIndex);
    }

    @Override
    public void fillObject(Profile.Section section) {
        super.fillObject(section);
        ObjectFactory.fill(this, section);
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
