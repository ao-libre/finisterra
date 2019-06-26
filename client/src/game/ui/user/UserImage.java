package game.ui.user;

import com.artemis.E;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import game.screens.GameScreen;
import game.utils.Colors;
import game.utils.Fonts;
import game.utils.Skins;

import static com.artemis.E.E;

public class UserImage extends ImageButton {

    UserImage() {
        super(Skins.COMODORE_SKIN, "big-disc");
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        drawLevel(batch);
    }

    private void drawLevel(Batch batch) {
        BitmapFont font = Fonts.WHITE_FONT;
        Fonts.layout.setText(font, getLevel(), Colors.GREY, getWidth() - 2, Align.center, true);
        font.draw(batch, Fonts.layout, getX(), getY() + getHeight() / 2);
    }

    private String getExp() {
        int playerId = GameScreen.getPlayer();
        if (playerId != -1) {
            E player = E(playerId);
            if (player.hasLevel()) {
                return "Exp: " + player.getLevel().exp + "/" + player.getLevel().expToNextLevel;
            }
        }
        return "";
    }

    private String getLevel() {
        int playerId = GameScreen.getPlayer();
        if (playerId != -1) {
            E player = E(playerId);
            if (player.hasLevel()) {
                return "Lv. " + player.getLevel().level;
            }
        }
        return "";
    }
}
