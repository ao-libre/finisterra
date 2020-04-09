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

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import game.systems.resources.AnimationsSystem;
import model.textures.AOImage;
import model.textures.AOTexture;

public class AOImageActor extends Image {


    private AOImage image;

    public AOImageActor(AOImage image, AnimationsSystem animationsSystem) {
        super();
        this.image = image;
        setScaling(Scaling.fit);
        if (image != null) {
            if (animationsSystem.hasTexture(image.getId())) {
                AOTexture texture = animationsSystem.getTexture(image.getId());
                if (texture != null) {
                    TextureRegion texture1 = texture.getTexture();
                    if (texture1.isFlipY()) {
                        texture1.flip(false, true);
                    }
                    setDrawable(new TextureRegionDrawable(texture1));
                }
            }
        }
    }

    public AOImage getImage() {
        return image;
    }

}
