/*******************************************************************************
 * Copyright (C) 2014  Rodrigo Troncoso
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package model.textures;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Array;
import game.handlers.DescriptorHandler;
import shared.model.Graphic;

public class BundledAnimation {

    private Array<GameTexture> frames = new Array<GameTexture>();
    private Animation<TextureRegion> animation;
    private float idleTime;
    private float idleBounce;
    private boolean bounce;
    private float animationTime;
    private boolean animated = false;
    private int times;
    private int loops;

    public BundledAnimation(Graphic graphic, boolean pingpong, int loops) {
        this.loops = loops;

        int numFrames = graphic.getFrames().length;
        Array<TextureRegion> tmpRegions = new Array<>();
        this.animationTime = 0.0f;

        if (numFrames > 0) {

            for (int frame : graphic.getFrames()) {
                this.frames.add(new GameTexture(DescriptorHandler.getGraphic(frame)));
                tmpRegions.add(this.frames.peek().getGraphic());
            }

            // TODO : Manual array conversion por error en toArray de gdxArray
            TextureRegion[] textures = new TextureRegion[tmpRegions.size];
            int index = 0;
            for (TextureRegion tmpTex : tmpRegions) {
                textures[index] = tmpTex;
                index++;
            }

            Animation<TextureRegion> animation = new Animation<>(graphic.getSpeed() / (1000.0f * 3.334f), Array.with(textures), pingpong ? Animation.PlayMode.LOOP_PINGPONG : Animation.PlayMode.NORMAL);
            this.setAnimation(animation);
            this.animated = true;
        } else {
            this.frames.add(new GameTexture(graphic));
        }
    }

    public BundledAnimation(Graphic graphic, boolean pingpong) {
        this(graphic, pingpong, 1);
    }

    public BundledAnimation(Graphic graphic, int loops) {
        this(graphic, false, loops);
    }

    /**
     * @return the frames
     */
    public Array<GameTexture> getFrames() {
        return frames;
    }

    /**
     * @param frames the frames to set
     */
    public void setFrames(Array<GameTexture> frames) {
        this.frames = frames;
    }

    /**
     * @return the animation
     */
    public Animation getAnimation() {
        return animation;
    }

    /**
     * @param animation the animation to set
     */
    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    /**
     * @return TextureRegion
     */
    public TextureRegion getGraphic(boolean loop) {
        return this.isAnimated() ? this.getAnimatedGraphic(loop) : this.getGraphic(0);
    }

    /**
     * @return TextureRegion
     */
    public TextureRegion getGraphic() {
        return this.getGraphic(true);
    }

    /**
     * @param index
     * @return TextureRegion
     */
    public TextureRegion getGraphic(int index) {
        return this.frames.get(index).getGraphic();
    }

    /**
     * @param loop
     * @return TextureRegion
     */
    public TextureRegion getAnimatedGraphic(boolean loop) {
        return this.animation.getKeyFrame(this.getAnimationTime());
    }

    /**
     * @return boolean
     */
    public boolean isAnimated() {
        return this.animated;
    }

    /**
     * @return the animationTime
     */
    public float getAnimationTime() {
        return animationTime;
    }

    /**
     * @param animationTime the animationTime to set
     */
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

    public int getCurrentFrameIndex() {
        return animation.getKeyFrameIndex(animationTime % (animation.getFrameDuration() * frames.size));
    }

}
