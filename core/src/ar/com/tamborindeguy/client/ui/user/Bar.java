package ar.com.tamborindeguy.client.ui.user;

import ar.com.tamborindeguy.client.screens.GameScreen;
import ar.com.tamborindeguy.client.utils.Colors;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.sun.javafx.text.GlyphLayout;

import static ar.com.tamborindeguy.client.utils.Fonts.WHITE_FONT_WITH_BORDER;
import static ar.com.tamborindeguy.client.utils.Fonts.layout;
import static com.artemis.E.E;

public class Bar extends Actor {

    static final int TOTAL_WIDTH = 200;
    private static final int ICON_SIZE = 16;
    private static final int SPACE_WIDTH = 4;
    private static final int INTER_SPACE = 4;
    private static final int BAR_WIDTH = TOTAL_WIDTH - (SPACE_WIDTH * 2) - ICON_SIZE - INTER_SPACE;
    static final int TOTAL_HEIGHT = 20;
    private static final int BAR_HEIGHT = 8;
    private static Texture backgroundTexture = new Texture(Gdx.files.local("data/ui/images/bar.png"));
    private final static Texture HEALTH = new Texture(Gdx.files.local("data/ui/images/heart.png"));
    private final static Texture MAGIC = new Texture(Gdx.files.local("data/ui/images/magic.png"));
    private final static Texture ENERGY = new Texture(Gdx.files.local("data/ui/images/energy.png"));

    private Drawable background;
    private Drawable bar;
    private Kind kind;

    public Bar(Kind kind) {
        this.kind = kind;
        background = new TextureRegionDrawable(new TextureRegion(backgroundTexture));
        bar = new TextureRegionDrawable(new TextureRegion(backgroundTexture));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color originalColor = batch.getColor();

        float barX = getX() + SPACE_WIDTH + ICON_SIZE + INTER_SPACE;
        float barY = getY() + (float) BAR_HEIGHT / 2;
        int player = GameScreen.getPlayer();
        int min = 0, max = 0;
        switch (kind) {
            case HP:
                min = E(player).getHealth().min;
                max = E(player).getHealth().max;
                break;
            case MANA:
                min = E(player).getMana().min;
                max = E(player).getMana().max;
                break;
            case ENERGY:
                min = E(player).getStamina().min;
                max = E(player).getStamina().max;
        }

        drawBar(batch, barX, barY, min, max);
        drawText(batch, min, max, barX, barY);
        batch.setColor(originalColor);
    }

    private void drawBar(Batch batch, float barX, float barY, float min, int max) {
        batch.setColor(Color.BLACK);
        background.draw(batch, barX, barY, BAR_WIDTH, BAR_HEIGHT);
        batch.setColor(Color.WHITE);
        kind.getIcon().draw(batch, getX() + SPACE_WIDTH, getY(), ICON_SIZE, ICON_SIZE);
        batch.setColor(kind.getColor());
        bar.draw(batch, barX, barY, min / max * BAR_WIDTH, BAR_HEIGHT);
    }

    private void drawText(Batch batch, int min, int max, float barX, float barY) {
        BitmapFont font = WHITE_FONT_WITH_BORDER;
        layout.setText(font, min + "/" + max, Color.WHITE, BAR_WIDTH - 4, GlyphLayout.LAYOUT_RIGHT_TO_LEFT, false);
        font.draw(batch, layout, barX, barY + BAR_HEIGHT);
    }

    enum Kind {
        HP(Colors.HEALTH, new TextureRegionDrawable(new TextureRegion(HEALTH))),
        MANA(Colors.MANA, new TextureRegionDrawable(new TextureRegion(MAGIC))),
        ENERGY(Color.YELLOW, new TextureRegionDrawable(new TextureRegion(Bar.ENERGY)));

        private Color color;
        private Drawable icon;

        Kind(Color color, Drawable icon) {
            this.color = color;
            this.icon = icon;
        }

        public Color getColor() {
            return color;
        }

        public Drawable getIcon() {
            return icon;
        }
    }
}
