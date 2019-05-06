package shared.objects.types;

public class DoorObj extends Obj {
    private boolean openDoor;
    private boolean hasKey;
    private int openIndex;
    private int closeIndex;
    private int closeKeyIndex;

    public DoorObj() {
    }

    public DoorObj(int id, String name, int grhIndex) {
        super(id, name, grhIndex);
    }

    @Override
    public Type getType() {
        return Type.DOOR;
    }

    public int getCloseIndex() {
        return closeIndex;
    }

    public void setCloseIndex(int closeIndex) {
        this.closeIndex = closeIndex;
    }

    public int getCloseKeyIndex() {
        return closeKeyIndex;
    }

    public void setCloseKeyIndex(int closeKeyIndex) {
        this.closeKeyIndex = closeKeyIndex;
    }

    public boolean isHasKey() {
        return hasKey;
    }

    public void setHasKey(boolean hasKey) {
        this.hasKey = hasKey;
    }

    public int getOpenIndex() {
        return openIndex;
    }

    public void setOpenIndex(int openIndex) {
        this.openIndex = openIndex;
    }

    public boolean getOpenDoor() {
        return openDoor;
    }

    public void setOpenDoor(boolean openDoor) {
        this.openDoor = openDoor;
    }
}
