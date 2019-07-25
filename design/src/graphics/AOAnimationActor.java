/*
 * The MIT License
 *
 * Copyright 2018 Raymond Buckley.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.minlog.Log;
import design.screens.views.DescriptorActor;
import entity.character.states.Heading;
import game.handlers.AnimationHandler;
import model.descriptors.Descriptor;
import model.descriptors.FXDescriptor;
import model.textures.BundledAnimation;

public class AOAnimationActor extends DescriptorActor {

    private BundledAnimation animation;
    private AnimationHandler animationHandler;
    private int heading = Heading.HEADING_SOUTH;
    private Descriptor descriptor;

    public AOAnimationActor(AnimationHandler animationHandler) {
        super();
        this.animationHandler = animationHandler;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (animation != null) {
            animation.setAnimationTime(animation.getAnimationTime() + delta);
            setSize(animation.getGraphic().getRegionWidth(), animation.getGraphic().getRegionHeight());
        }
    }

    @Override
    public void move() {

    }

    @Override
    public void rotate() {
        heading = MathUtils.clamp((heading + 1) % 4, Heading.HEADING_NORTH, Heading.HEADING_WEST);
        if (descriptor != null) {
            setAnimation(descriptor);
        }
    }

    public void setDescriptor(Descriptor descriptor) {
        this.descriptor = descriptor;
        if (descriptor == null) {
            this.animation = null;
            setSize(0,0);
        } else {
            setAnimation(descriptor);
        }
    }

    public void setAnimation(Descriptor descriptor) {
        int graphic = descriptor instanceof FXDescriptor ? descriptor.getGraphic(0) : descriptor.getGraphic(heading);
        if (graphic > 0) {
            this.animation = animationHandler.getAnimation(graphic);
        } else {
            Log.info("Failed to preview descriptor: " + descriptor);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (animation != null) {
            batch.draw(animation.getGraphic(), getX(), getY());
        }
    }

}
