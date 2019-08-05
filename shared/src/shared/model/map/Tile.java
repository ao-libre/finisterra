package shared.model.map;

import com.google.common.base.Objects;

import java.util.Arrays;

public class Tile {

    public static final int EMPTY_INDEX = -1;
    public static final float TILE_PIXEL_WIDTH = 64.0f;
    public static final float TILE_PIXEL_HEIGHT = 64.0f;

    private int[] graphic;

    private int charIndex;
    private int objIndex;
    private int objCount;
    private int npcIndex;

    private WorldPosition tileExit;
    private boolean blocked;

    private int trigger;

    public Tile() {
        graphic = new int[4];
    }

    public Tile(Tile other) {
        this.graphic = Arrays.copyOf(other.graphic, other.graphic.length);
        this.charIndex = other.charIndex;
        this.objIndex = other.objIndex;
        this.objCount = other.objCount;
        this.npcIndex = other.npcIndex;
        this.tileExit = other.tileExit;
        this.blocked = other.blocked;
        this.trigger = other.trigger;
    }

    public Tile(int[] graphic, int charIndex, int objCount, int objIndex,
                int npcIndex, WorldPosition tileExit, boolean blocked,
                int trigger) {
        this.setGraphic(graphic);
        this.setCharIndex(charIndex);
        this.setObjIndex(objIndex);
        this.setNpcIndex(npcIndex);
        this.setTileExit(tileExit);
        this.setBlocked(blocked);
        this.setTrigger(trigger);
        this.setObjCount(objCount);
    }

    public int getObjCount() {
        return objCount;
    }

    public void setObjCount(int objCount) {
        this.objCount = objCount;
    }

    public int getGraphic(int index) {
        return this.graphic[index];
    }

    public int[] getGraphic() {
        return this.graphic;
    }

    public void setGraphic(int[] graphic) {
        this.graphic = graphic;
    }

    public int getCharIndex() {
        return charIndex;
    }

    public void setCharIndex(int charIndex) {
        this.charIndex = charIndex;
    }

    public int getObjIndex() {
        return objIndex;
    }

    public void setObjIndex(int objIndex) {
        this.objIndex = objIndex;
    }

    public int getNpcIndex() {
        return npcIndex;
    }

    public void setNpcIndex(int npcIndex) {
        this.npcIndex = npcIndex;
    }

    public WorldPosition getTileExit() {
        return tileExit;
    }

    public void setTileExit(WorldPosition tileExit) {
        this.tileExit = tileExit;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public int getTrigger() {
        return trigger;
    }

    public void setTrigger(int trigger) {
        this.trigger = trigger;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return charIndex == tile.charIndex &&
                objIndex == tile.objIndex &&
                objCount == tile.objCount &&
                npcIndex == tile.npcIndex &&
                blocked == tile.blocked &&
                trigger == tile.trigger &&
                sameGraphics(tile) &&
                Objects.equal(tileExit, tile.tileExit);
    }

    public boolean sameGraphics(Tile tile) {
        boolean result = true;
        for (int i = 0; i < 4; i++) {
            result &= graphic[i] == tile.graphic[i];
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(graphic, charIndex, objIndex, objCount, npcIndex, tileExit, blocked, trigger);
    }
}
