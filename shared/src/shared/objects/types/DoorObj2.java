package shared.objects.types;

public class DoorObj2 extends Obj {

    private boolean open = false;
    private int closedGrh;
    private int tileHeight = 1;
    private int tileWidth = 1;

    public DoorObj2() {
    }

    public DoorObj2(int id, String name, int grhIndex) {
        super(id, name, grhIndex);
    }

    @Override
    public Type getType() {
        return Type.DOOR;
    }

    public int getClosedGrh() {
        return closedGrh;
    }

    public void setClosedGrh(int closedGrh) {
        this.closedGrh = closedGrh;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public void setTileHeight(int tileHeight) {
        this.tileHeight = tileHeight;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public void setTileWidth(int tileWidth) {
        this.tileWidth = tileWidth;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public int getOpenGrh() {
        return getGrhIndex();
    }

}
