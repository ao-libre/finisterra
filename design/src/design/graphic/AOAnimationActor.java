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

package design.graphic;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.minlog.Log;
import design.screens.ScreenEnum;
import design.screens.views.DescriptorActor;
import component.entity.character.states.Heading;
import game.systems.resources.AnimationsSystem;
import model.descriptors.Descriptor;
import model.descriptors.FXDescriptor;
import model.textures.AOAnimation;
import model.textures.BundledAnimation;

public class AOAnimationActor extends DescriptorActor {

    private BundledAnimation animation;
    private AnimationsSystem animationsSystem;
    private int heading = Heading.HEADING_SOUTH;
    private Descriptor descriptor;

    public AOAnimationActor(AnimationsSystem animationsSystem) {
        super();
        this.animationsSystem = animationsSystem;
    }

    //@fixme
//    public AOAnimationActor(AOAnimation animation) {
//        this(new BundledAnimation(animation));
//    }

    public AOAnimationActor(BundledAnimation animation) {
        super();
        this.animation = animation;
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

    @Override
    public void setDescriptor(Descriptor descriptor) {
        this.descriptor = descriptor;
        if (descriptor == null) {
            this.animation = null;
            setSize(0, 0);
        } else {
            setAnimation(descriptor);
        }
    }

    public void setAnimation(Descriptor descriptor) {

        try {
            int graphic;

            if (descriptor instanceof FXDescriptor) {
                graphic = descriptor.getGraphic(0);
            } else {
                graphic = descriptor.getGraphic(heading);
            }

            setAnimationID(graphic);

        } catch (NullPointerException ex) {
            Log.error(this.toString(), "Non-existing graphic.", ex);
        }


    }

    public void setAnimation(AOAnimation aoAnimation) {
        this.animation = animationsSystem.createAnimation(aoAnimation);
    }

    public void setAnimationID(int graphic) {
        if (graphic > 0) {
            ScreenEnum.ANIMATION_VIEW
                    .getScreen()
                    .getDesigner()
                    .get(graphic)
                    .ifPresent(anim -> this.setAnimation((AOAnimation) anim));
        } else {
            Log.info(this.toString(), "Failed to preview descriptor: " + descriptor);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (animation != null) {
            batch.draw(animation.getGraphic(), getX(), getY());
        }
    }

}
