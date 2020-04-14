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
import design.screens.views.DescriptorActor;
import component.entity.character.states.Heading;
import game.systems.resources.AnimationsSystem;
import model.descriptors.Descriptor;
import model.textures.AOTexture;

public class StaticDescriptorActor extends DescriptorActor {

    private final AnimationsSystem animationsSystem;
    private int heading = Heading.HEADING_SOUTH;
    private Descriptor descriptor;
    private AOTexture texture;

    public StaticDescriptorActor(AnimationsSystem animationsSystem) {
        super();
        this.animationsSystem = animationsSystem;
    }

    @Override
    public void move() {

    }

    @Override
    public void rotate() {
        heading = MathUtils.clamp((heading + 1) % 4, Heading.HEADING_NORTH, Heading.HEADING_WEST);
        if (descriptor != null) {
            texture = animationsSystem.getTexture(descriptor.getGraphic(heading));
        }
    }

    @Override
    public void setDescriptor(Descriptor descriptor) {
        this.descriptor = descriptor;
        if (descriptor == null) {
            this.texture = null;
            setSize(0, 0);
        } else {
            int graphic = descriptor.getGraphic(heading);
            if (graphic > 0) {
                this.texture = animationsSystem.getTexture(graphic);
                setSize(texture.getTexture().getRegionWidth(), texture.getTexture().getRegionHeight());
            } else {
                Log.info(this.toString(), "Fail to show preview for descriptor: " + descriptor);
                this.texture = null;
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (texture != null) {
            batch.draw(texture.getTexture(), getX(), getY());
        }
    }

}
