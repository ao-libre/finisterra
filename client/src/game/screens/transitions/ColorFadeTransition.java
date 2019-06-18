package game.screens.transitions;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;

/**
 * A color fading transition
 *
 * @author iXeption
 */
public class ColorFadeTransition implements ScreenTransition {

    private final Color color;
    private final Interpolation interpolation;
    private final Texture texture;

    /**
     * @param color         the {@link Color} to fade to
     * @param interpolation the {@link Interpolation} method
     */
    public ColorFadeTransition(Color color, Interpolation interpolation) {
        this.color = new Color(Color.WHITE);
        this.interpolation = interpolation;

        texture = new Texture(1, 1, Format.RGBA8888);
        Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillRectangle(0, 0, 1, 1);
        texture.draw(pixmap, 0, 0);
    }

    @Override
    public void render(Batch batch, Texture currentScreenTexture, Texture nextScreenTexture, float percent) {
        float width = currentScreenTexture.getWidth();
        float height = currentScreenTexture.getHeight();
        float x = 0;
        float y = 0;

        if (interpolation != null) percent = interpolation.apply(percent);

        batch.begin();

        float fade = percent * 2;

        if (fade > 1.0f) {
            fade = 1.0f - (percent * 2 - 1.0f);
            color.a = 1.0f - fade;
            batch.setColor(color);

            batch.draw(nextScreenTexture, 0, 0, width / 2, height / 2, nextScreenTexture.getWidth(), nextScreenTexture.getHeight(),
                    1, 1, 0, 0, 0, nextScreenTexture.getWidth(), nextScreenTexture.getHeight(), false, true);

        } else {

            color.a = 1.0f - fade;
            batch.setColor(color);

            batch.draw(currentScreenTexture, 0, 0, width / 2, height / 2, width, height, 1, 1, 0, 0, 0, (int) width, (int) height,
                    false, true);

        }

        color.a = fade;

        batch.setColor(color);
        batch.draw(texture, 0, 0, width, height);
        batch.end();
        batch.setColor(Color.WHITE);

    }

}
