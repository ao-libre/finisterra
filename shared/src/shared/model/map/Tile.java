package shared.model.map;

import java.util.Optional;

public class Tile {

	public static final int EMPTY_INDEX = -1;
	public static final float TILE_PIXEL_WIDTH = 32.0f;
	public static final float TILE_PIXEL_HEIGHT = 32.0f;

	private int[] graphic;

	private int charIndex = EMPTY_INDEX;
	private int objIndex = EMPTY_INDEX;
	private int npcIndex = EMPTY_INDEX;
	
	private WorldPosition tileExit = new WorldPosition();
	private boolean blocked;

	private int trigger;

	public Tile() {}

	public Tile(int[] graphic, int charIndex, int objIndex,
				int npcIndex, WorldPosition tileExit, boolean blocked,
				int trigger) {
		this.setGraphic(graphic);
		this.setCharIndex(charIndex);
		this.setObjIndex(objIndex);
		this.setNpcIndex(npcIndex);
		this.setTileExit(Optional.ofNullable(tileExit).orElse(new WorldPosition(0, 0, 0)));
		this.setBlocked(blocked);
		this.setTrigger(trigger);
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

}
