package game.ui.user;

import com.artemis.E;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import game.screens.GameScreen;
import game.utils.Colors;
import game.utils.Skins;

import static com.artemis.E.E;

public class Bar extends Actor {

    //private static final int ORIGINAL_WIDTH = 212;
    private static final int ORIGINAL_HEIGHT = 32;
    private static final int ORIGINAL_BORDER = 9;
    private static final Drawable background = Skins.COMODORE_SKIN.getDrawable("bar-frame");
    private final Label points;
    private final Drawable bar = Skins.COMODORE_SKIN.getDrawable("bar");
    private final Kind kind;

    Bar(Kind kind) {
        this.kind = kind;
        points = new Label("", Skins.COMODORE_SKIN, "bar");
        points.setAlignment(Align.right);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        int player = GameScreen.getPlayer();
        final E entity = E(player);
        if (entity == null) {
            return;
        }
        Color originalColor = batch.getColor();
        float barX = getX();
        float barY = getY();
        int min = 0, max = 0;
        switch (kind) {
            case HP:
                if (!entity.hasHealth()) {
                    return;
                }
                min = entity.getHealth().min;
                max = entity.getHealth().max;
                break;
            case MANA:
                if (!entity.hasMana()) {
                    return;
                }
                min = entity.getMana().min;
                max = entity.getMana().max;
                break;
            case ENERGY:
                if (!entity.hasStamina()) {
                    return;
                }
                min = entity.getStamina().min;
                max = entity.getStamina().max;
        }

        drawBar(batch, barX, barY, min, max);
        drawText(batch, min, max, barX, barY + 1);
        batch.setColor(originalColor);
    }

    private void drawBar(Batch batch, float barX, float barY, float min, int max) {
        float yFactor = getHeight() / ORIGINAL_HEIGHT;
        batch.setColor(Color.WHITE);
        background.draw(batch, barX, barY, getWidth(), getHeight());
        batch.setColor(kind.getColor());
        float percent = min / max;
        int barWidth = (int) (percent * (getWidth() - ORIGINAL_BORDER * 2));
        bar.draw(batch, barX + ORIGINAL_BORDER, barY + yFactor * ORIGINAL_BORDER, barWidth, yFactor * (ORIGINAL_HEIGHT - ORIGINAL_BORDER * 2));
    }

    private void drawText(Batch batch, int min, int max, float barX, float barY) {
        points.setText(min + "/" + max);
        points.setWidth(getWidth() - ORIGINAL_BORDER * 2);
        points.setPosition(barX, barY + ORIGINAL_BORDER + 2.5f);
        points.draw(batch, 1);
    }

    enum Kind {
        HP(Colors.HEALTH),
        MANA(Colors.MANA),
        ENERGY(Colors.YELLOW);

        private final Color color;

        Kind(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return color;
        }

    }
}
