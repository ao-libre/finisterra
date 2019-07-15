package model.textures;

public class AOAnimation {

    private int id;
    private int[] frames; // references to ao images
    private float speed;

    public AOAnimation() {
    }

    public AOAnimation(int id, int[] frames, float speed) {
        this.id = id;
        this.frames = frames;
        this.speed = speed;
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
