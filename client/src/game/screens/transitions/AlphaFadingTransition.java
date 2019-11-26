//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package game.screens.transitions;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;

public class AlphaFadingTransition implements ScreenTransition {
    public AlphaFadingTransition() {
    }

    @Override
    public void render(Batch batch, Texture currentScreenTexture, Texture nextScreenTexture, float alpha) {
        alpha = Interpolation.fade.apply(alpha);
        batch.begin();
        batch.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        batch.draw(currentScreenTexture, 0.0F, 0.0F, 0.0F, 0.0F, (float) currentScreenTexture.getWidth(), (float) currentScreenTexture.getHeight(), 1.0F, 1.0F, 0.0F, 0, 0, currentScreenTexture.getWidth(), currentScreenTexture.getHeight(), false, true);
        batch.setColor(1.0F, 1.0F, 1.0F, alpha);
        batch.draw(nextScreenTexture, 0.0F, 0.0F, 0.0F, 0.0F, (float) nextScreenTexture.getWidth(), (float) nextScreenTexture.getHeight(), 1.0F, 1.0F, 0.0F, 0, 0, nextScreenTexture.getWidth(), nextScreenTexture.getHeight(), false, true);
        batch.end();
    }
}
