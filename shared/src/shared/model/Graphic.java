package shared.model;

import shared.model.map.Tile;

// TODO move to client?
public class Graphic {

    private int x;
    private int y;
    private int fileNum;
    private int grhIndex;
    private int width;
    private int height;
    private int[] frames;
    private float speed;

    public Graphic() {
        this(0, 0, 0, 0, 0, 0.0f, 0.0f, new int[0], 0.0f);
    }

    public Graphic(int x, int y, int fileNum, int pixelWidth, int pixelHeight,
                   float tileWidth, float tileHeight, int[] frames, float speed) {
        this.x = x;
        this.y = y;
        this.fileNum = fileNum;
        this.width = pixelWidth;
        this.height = pixelHeight;
        this.frames = frames;
        this.speed = speed;
    }

    public int getX() {
        return x * 2;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y * 2;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getFileNum() {
        return fileNum;
    }

    public void setFileNum(int fileNum) {
        this.fileNum = fileNum;
    }

    public int getWidth() {
        return width * 2;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height * 2;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getTileWidth() {
        return width / Tile.TILE_PIXEL_WIDTH;
    }

    public float getTileHeight() {
        return height / Tile.TILE_PIXEL_HEIGHT;
    }

    public int[] getFrames() {
        return frames;
    }

    public void setFrames(int[] frames) {
        this.frames = frames;
    }

    public int getFrame(int pIndex) {
        return frames[pIndex];
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public int getGrhIndex() {
        return grhIndex;
    }

    public void setGrhIndex(int grhIndex) {
        this.grhIndex = grhIndex;
    }


}
