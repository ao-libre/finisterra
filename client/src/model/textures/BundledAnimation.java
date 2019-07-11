package model.textures;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Array;
import game.AssetManagerHolder;
import game.handlers.AOAssetManager;
import game.handlers.AnimationHandler;
import game.screens.WorldScreen;

import java.util.Arrays;

public class BundledAnimation {

    private Animation<TextureRegion> animation;
    private float idleTime;
    private float idleBounce;
    private boolean bounce;
    private float animationTime;
    private int times;
    private int loops;

    public BundledAnimation(AOAnimation anim, boolean pingpong, int loops) {
        TextureRegion[] textures = Arrays.stream(anim.getFrames()).filter(i -> i > 0).mapToObj(this::getTexture).toArray(TextureRegion[]::new);
        Animation<TextureRegion> animation = new Animation<>(anim.getSpeed() / (1000.0f * 3.334f), Array.with(textures), pingpong ? Animation.PlayMode.LOOP_PINGPONG : Animation.PlayMode.NORMAL);
        this.setAnimation(animation);
        this.loops = loops;
    }

    private TextureRegion getTexture(int id) {
        Game game = (Game) Gdx.app.getApplicationListener();
        WorldScreen screen = (WorldScreen) game.getScreen();
        AnimationHandler system = screen.getWorld().getSystem(AnimationHandler.class);
        return system.getTexture(id).getTexture();
    }

    public BundledAnimation(AOAnimation anim, boolean pingpong) {
        this(anim, pingpong, 1);
    }

    public BundledAnimation(AOAnimation anim) {
        this(anim, false, 1);
    }

    public BundledAnimation(AOAnimation anim, int loops) {
        this(anim, false, loops);
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