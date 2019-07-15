package model.textures;

import model.ID;

import static java.util.Arrays.copyOf;

public class AOAnimation implements ID {

    private int id;
    private int[] frames; // references to ao images
    private float speed;

    public AOAnimation() {
        frames = new int[4];
    }

    public AOAnimation(int id, int[] frames, float speed) {
        this.id = id;
        this.frames = frames;
        this.speed = speed;
    }

    public AOAnimation(AOAnimation other) {
        this.id = other.id;
        this.frames = copyOf(other.frames, other.frames.length);
        this.speed = other.speed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getSpeed() {
        return speed;
    }

    public int[] getFrames() {
        return frames;
    }

    public void setFrames(int[] frames) {
        this.frames = frames;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Override
    public String toString() {
        return getId() + ":" + " speed: " + getSpeed();
    }
}