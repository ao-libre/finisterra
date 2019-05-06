/**
 * Copyright (C) 2014  Rodrigo Troncoso
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * <p>
 * Now packs everything in GameTextures (previously known as BundledTexture)
 * treating everything as an animation
 *
 * @author Rodrigo Troncoso
 * @version 0.1
 * @author Rodrigo Troncoso
 * @version 0.1
 * @since 2014-04-17
 * <p>
 * Now packs everything in GameTextures (previously known as BundledTexture)
 * treating everything as an animation
 * @since 2014-04-17
 */
/**
 * Now packs everything in GameTextures (previously known as BundledTexture) 
 * treating everything as an animation
 * @author Rodrigo Troncoso
 * @version 0.1
 * @since 2014-04-17
 */
package model.textures;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import game.handlers.DescriptorHandler;
import shared.model.Graphic;

public class BundledTexture {

    private GameTexture[] frames;
    private Animation<TextureRegion> animation;
    private float animationTime;
    private boolean animated;

    public BundledTexture(Graphic graphic, boolean animated) {
        this.animationTime = 0.0f;

        if (!animated) {
            this.frames = new GameTexture[1];
            this.frames[0] = new GameTexture(graphic);
            this.animated = false;
        } else {
            int numFrames = graphic.getFrames().length;

            this.frames = new GameTexture[numFrames];
            TextureRegion tmpFrames[] = new TextureRegion[numFrames];
            for (int i = 0; i < numFrames; i++) {
                this.frames[i] = new GameTexture(DescriptorHandler.getGraphics().get(graphic.getFrame(i)));
                tmpFrames[i] = this.frames[i].getGraphic();
            }
            this.animation = new Animation<TextureRegion>(graphic.getSpeed() / 1000, tmpFrames);
            this.animated = true;
        }
    }

    public BundledTexture(int grhIndex, boolean animated) {
        this(DescriptorHandler.getGraphic(grhIndex), animated);
    }

    public BundledTexture(int grhIndex) {
        this(grhIndex, false);
    }

    public void dispose() {
        for (GameTexture t : this.frames) {
            t.dispose();
        }
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
     * @return the animationTime
     */
    public float getAnimationTime() {
        return animationTime;
    }

    /**
     * @param animationTime the animationTime to set
     */
    public void setAnimationTime(float animationTime) {
        this.animationTime = animationTime;
    }

    /**
     * @return the frames
     */
    public GameTexture[] getFrames() {
        return frames;
    }

    /**
     * @param mFrames the frames to set
     */
    public void setFrames(GameTexture[] mFrames) {
        this.frames = mFrames;
    }

    public TextureRegion getGraphic() {
        return this.getGraphic(0);
    }


    public TextureRegion getGraphic(int index) {
        return this.frames[index].getGraphic();
    }

    public TextureRegion getGraphic(boolean loop) {
        if (this.animated) {
            return this.animation.getKeyFrame(this.animationTime, loop);
        }

        return this.getGraphic();
    }

    /**
     * @return the animated
     */
    public boolean isAnimated() {
        return animated;
    }

    /**
     * @param mAnimated the animated to set
     */
    public void setAnimated(boolean mAnimated) {
        this.animated = mAnimated;
    }

}
