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
package ar.com.tamborindeguy.model.textures;

import ar.com.tamborindeguy.client.handlers.DescriptorHandler;
import ar.com.tamborindeguy.model.Graphic;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**
 * @author Rodrigo
 */
public class BundledAnimation {

    private Array<GameTexture> frames = new Array<GameTexture>();
    private Animation<TextureRegion> animation;
    private float animationTime;
    private boolean animated = false;


    public BundledAnimation(Graphic graphic) {

        int numFrames = graphic.getFrames().length;
        Array<TextureRegion> tmpRegions = new Array<TextureRegion>();
        this.setAnimationTime(0.0f);

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

            this.setAnimation(new Animation<TextureRegion>(graphic.getSpeed() / 3340.0f, textures));
            this.animated = true;

        } else {
            this.frames.add(new GameTexture(graphic));
        }
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
        return this.animation.getKeyFrame(this.getAnimationTime(), loop);
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
        this.animationTime = animationTime;
    }


}
