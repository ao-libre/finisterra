//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package game.screens.transitions;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;

public class SlidingTransition implements ScreenTransition {
    private SlidingTransition.Direction direction;
    private boolean slideOut;
    private Interpolation interpolation;

    public SlidingTransition(SlidingTransition.Direction direction, Interpolation interpolation, boolean slideOut) {
        this.direction = direction;
        this.interpolation = interpolation;
        this.slideOut = slideOut;
    }

    public void render(Batch batch, Texture currentScreenTexture, Texture nextScreenTexture, float percent) {
        float width = (float) currentScreenTexture.getWidth();
        float height = (float) currentScreenTexture.getHeight();
        float x = 0.0F;
        float y = 0.0F;
        if (this.interpolation != null) {
            percent = this.interpolation.apply(percent);
        }

        switch (this.direction) {
            case LEFT:
                x = -width * percent;
                if (!this.slideOut) {
                    x += width;
                }
                break;
            case RIGHT:
                x = width * percent;
                if (!this.slideOut) {
                    x -= width;
                }
                break;
            case UP:
                y = height * percent;
                if (!this.slideOut) {
                    y -= height;
                }
                break;
            case DOWN:
                y = -height * percent;
                if (!this.slideOut) {
                    y += height;
                }
        }

        Texture texBottom = this.slideOut ? nextScreenTexture : currentScreenTexture;
        Texture texTop = this.slideOut ? currentScreenTexture : nextScreenTexture;
        batch.begin();
        batch.draw(texBottom, 0.0F, 0.0F, 0.0F, 0.0F, width, height, 1.0F, 1.0F, 0.0F, 0, 0, (int) width, (int) height, false, true);
        batch.draw(texTop, x, y, 0.0F, 0.0F, (float) nextScreenTexture.getWidth(), (float) nextScreenTexture.getHeight(), 1.0F, 1.0F, 0.0F, 0, 0, nextScreenTexture.getWidth(), nextScreenTexture.getHeight(), false, true);
        batch.end();
    }

    public static enum Direction {
        LEFT,
        RIGHT,
        UP,
        DOWN;

        private Direction() {
        }
    }
}
