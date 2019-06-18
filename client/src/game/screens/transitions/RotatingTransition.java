//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package game.screens.transitions;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;

public class RotatingTransition implements ScreenTransition {
    private Interpolation interpolation;
    private float angle;
    private RotatingTransition.TransitionScaling scaling;

    public RotatingTransition(Interpolation interpolation, float angle, RotatingTransition.TransitionScaling scaling) {
        this.interpolation = interpolation;
        this.angle = angle;
        this.scaling = scaling;
    }

    public void render(Batch batch, Texture currentScreenTexture, Texture nextScreenTexture, float percent) {
        float width = (float) currentScreenTexture.getWidth();
        float height = (float) currentScreenTexture.getHeight();
        float x = 0.0F;
        float y = 0.0F;
        float scalefactor;
        switch (this.scaling) {
            case IN:
                scalefactor = percent;
                break;
            case OUT:
                scalefactor = 1.0F - percent;
                break;
            case NONE:
            default:
                scalefactor = 1.0F;
        }

        float rotation = 1.0F;
        if (this.interpolation != null) {
            rotation = this.interpolation.apply(percent);
        }

        batch.begin();
        batch.draw(currentScreenTexture, 0.0F, 0.0F, width / 2.0F, height / 2.0F, width, height, 1.0F, 1.0F, 0.0F, 0, 0, (int) width, (int) height, false, true);
        batch.draw(nextScreenTexture, 0.0F, 0.0F, width / 2.0F, height / 2.0F, (float) nextScreenTexture.getWidth(), (float) nextScreenTexture.getHeight(), scalefactor, scalefactor, rotation * this.angle, 0, 0, nextScreenTexture.getWidth(), nextScreenTexture.getHeight(), false, true);
        batch.end();
    }

    public static enum TransitionScaling {
        NONE,
        IN,
        OUT;

        private TransitionScaling() {
        }
    }
}
