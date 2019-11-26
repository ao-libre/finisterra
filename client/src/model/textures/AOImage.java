package model.textures;

import model.ID;

public class AOImage implements ID {

    private int x;
    private int y;
    private int fileNum;
    private int id;
    private int width;
    private int height;

    public AOImage() {
    }

    public AOImage(AOImage other) {
        this.x = other.x;
        this.y = other.y;
        this.fileNum = other.fileNum;
        this.id = other.id;
        this.width = other.width;
        this.height = other.height;
    }

    public AOImage(int id, int x, int y, int fileNum, int pixelWidth, int pixelHeight) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.fileNum = fileNum;
        this.width = pixelWidth;
        this.height = pixelHeight;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
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
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return getId() + ":" + " file: " + getFileNum() + " x: " + getX() + " y: " + getY();
    }

    public void adjust() {
        setX(getX());
        setY(getY());
        setWidth(getWidth());
        setHeight(getHeight());
    }
}
