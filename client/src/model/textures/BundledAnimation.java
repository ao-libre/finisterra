package model.textures;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Array;

public class BundledAnimation {

    private Animation<TextureRegion> animation;
    private float idleTime;
    private float idleBounce;
    private boolean bounce;
    private float animationTime;
    private int times;
    private int loops;

    public BundledAnimation(TextureRegion[] textures, float speed, boolean pingpong, int loops) {
        Animation<TextureRegion> localAnimation = new Animation<>(speed / (1000.0f * 3.334f), Array.with(textures), pingpong ? Animation.PlayMode.LOOP_PINGPONG : Animation.PlayMode.NORMAL);
        this.setAnimation(localAnimation);
        this.loops = loops;
    }

    public BundledAnimation(TextureRegion[] textures, float speed, boolean pingpong) {
        this(textures, speed, pingpong, 1);
    }

    public BundledAnimation(TextureRegion[] textures, float speed) {
        this(textures, speed, false, 1);
    }

    public BundledAnimation(TextureRegion[] textures, float speed, int loops) {
        this(textures, speed, false, loops);
    }

    public Animation getAnimation() {
        return animation;
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    public TextureRegion getGraphic() {
        return this.animation.getKeyFrame(this.getAnimationTime());
    }

    public TextureRegion getPreviousGraphic() {
        int index = this.animation.getKeyFrameIndex(this.getAnimationTime());
        return this.animation.getKeyFrames()[index == 0 ? this.animation.getKeyFrames().length - 1 : index - 1];
    }

    public float getPreviousFrameTransparency() {
        return 1 - (getAnimationTime() % this.animation.getKeyFrames().length) / this.animation.getFrameDuration();
    }

    public TextureRegion getGraphic(int index) {
        return this.animation.getKeyFrames()[index];
    }

    public float getAnimationTime() {
        return animationTime;
    }

    public void setAnimationTime(float animationTime) {
        if (animationTime >= animation.getAnimationDuration()) {
            times++;
        } else if (animationTime == 0) {
            times = 0;
        }
        this.animationTime = animationTime > animation.getAnimationDuration() ? animationTime - animation.getAnimationDuration() : animationTime;
    }

    public float getIdleTime() {
        return idleTime;
    }

    public void addDeltaIdleTime(float delta) {
        if (bounce) {
            idleBounce += delta;
            idleTime = Interpolation.circle.apply(idleBounce);
            bounce = idleBounce < 0.8f;
        } else {
            idleBounce -= delta;
            idleTime = Interpolation.circle.apply(idleBounce);
            bounce = idleBounce < 0f;
        }
    }

    public boolean isAnimationFinished() {
        return times >= loops;
    }

    public void setFrameDuration(float duration) {
        animation.setFrameDuration(duration / animation.getKeyFrames().length * 2 / 10f);
    }

}
