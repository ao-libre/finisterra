//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package game.screens.transitions;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Array;

public class SlicingTransition implements ScreenTransition {
    private final SlicingTransition.Direction direction;
    private final Interpolation interpolation;
    private final Array<Integer> slices = new Array<>();

    public SlicingTransition(SlicingTransition.Direction direction, int numSlices, Interpolation interpolation) {
        this.direction = direction;
        this.interpolation = interpolation;
        this.slices.clear();

        for (int i = 0; i < numSlices; ++i) {
            this.slices.add(i);
        }

        this.slices.shuffle();
    }

    @Override
    public void render(Batch batch, Texture currentScreenTexture, Texture nextScreenTexture, float percent) {
        float width = (float) currentScreenTexture.getWidth();
        float height = (float) currentScreenTexture.getHeight();
        float x;
        float y = 0.0F;
        int sliceWidth = (int) (width / (float) this.slices.size);
        batch.begin();
        batch.draw(currentScreenTexture, 0.0F, 0.0F, 0.0F, 0.0F, width, height, 1.0F, 1.0F, 0.0F, 0, 0, (int) width, (int) height, false, true);
        if (this.interpolation != null) {
            percent = this.interpolation.apply(percent);
        }

        for (int i = 0; i < this.slices.size; ++i) {
            x = (float) (i * sliceWidth);
            float offsetY = height * (1.0F + (float) this.slices.get(i) / (float) this.slices.size);
            switch (this.direction) {
                case UP:
                    y = -offsetY + offsetY * percent;
                    break;
                case DOWN:
                    y = offsetY - offsetY * percent;
                    break;
                case UPDOWN:
                    if (i % 2 == 0) {
                        y = -offsetY + offsetY * percent;
                    } else {
                        y = offsetY - offsetY * percent;
                    }
            }

            batch.draw(nextScreenTexture, x, y, 0.0F, 0.0F, (float) sliceWidth, (float) nextScreenTexture.getHeight(), 1.0F, 1.0F, 0.0F, i * sliceWidth, 0, sliceWidth, nextScreenTexture.getHeight(), false, true);
        }

        batch.end();
    }

    public enum Direction {
        UP,
        DOWN,
        UPDOWN;

        Direction() {
        }
    }
}
