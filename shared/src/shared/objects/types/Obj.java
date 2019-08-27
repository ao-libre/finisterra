package shared.objects.types;

import org.ini4j.Profile;
import shared.objects.factory.ObjectFactory;

public abstract class Obj implements IFillObject {

    private int id;
    private String name;
    private int grhIndex;
    private boolean collectable = false;
    private int value = 0;
    private boolean crucial = true;
    private boolean newbie = false;
    private boolean notDrop = false;

    public Obj() {
    }

    protected Obj(int id, String name, int grhIndex) {
        this.id = id;
        this.name = name;
        this.grhIndex = grhIndex;
    }

    public abstract Type getType();

    public String getName() {
        return name;
    }

    public int getGrhIndex() {
        return grhIndex;
    }

    public boolean isCollectable() {
        return collectable;
    }

    public void setCollectable(boolean collectable) {
        this.collectable = collectable;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isCrucial() {
        return crucial;
    }

    public void setCrucial(boolean crucial) {
        this.crucial = crucial;
    }

    public boolean isNewbie() {
        return newbie;
    }

    public void setNewbie(boolean newbie) {
        this.newbie = newbie;
    }

    public boolean isNotDrop() {
        return notDrop;
    }

    public void setNotDrop(boolean notDrop) {
        this.notDrop = notDrop;
    }

    @Override
    public void fillObject(Profile.Section section) {
        ObjectFactory.fillCommon(this, section);
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return getId() + ":" + getType().toString() + " name: " + getName();
    }
}

