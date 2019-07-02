package game.ui.user;

import com.artemis.E;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.utils.Align;
import entity.character.status.Level;
import game.screens.GameScreen;
import game.utils.Colors;
import game.utils.Fonts;
import game.utils.Skins;
import model.textures.RadialProgress;
import model.textures.RadialSprite;

import java.util.Optional;

import static com.artemis.E.E;

public class UserImage extends ImageButton {

    private final RadialProgress radialProgress;
    private final RadialSprite radialSprite;

    UserImage() {
        super(Skins.COMODORE_SKIN, "big-disc");
        radialSprite = new RadialSprite(Skins.COMODORE_SKIN.getRegion("disc-glow"));
        radialProgress = new RadialProgress(Skins.COMODORE_SKIN.getRegion("disc-glow"));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        getLevel().ifPresent(level -> {
            paintCircle(batch, level);
            drawLevel(batch, level);
        });
    }

    private void paintCircle(Batch batch, Level level) {
        float percent = getPercent(level);
        float angle = 360 - (360 * percent % 360);
        radialSprite.setAngle(angle);
        radialSprite.setScale(0.8f, 0.8f);
        radialSprite.draw(batch, getX() + 15, getY() + 15, getWidth() - 15, getHeight() - 15);
//        radialProgress.setPercent((int) getPercent(level));
//        radialProgress.draw((SpriteBatch) batch);
    }

    private float getPercent(Level level) {
        return (float) level.exp / (float) level.expToNextLevel * 100f;
    }

    private void drawLevel(Batch batch, Level level) {
        BitmapFont font = Fonts.WHITE_FONT_WITH_BORDER;
        Fonts.layout.setText(font, getLevelLabel(level) + " - " + getExp(level), Colors.GREY, getWidth() - 2, Align.center, true);
        font.draw(batch, Fonts.layout, getX(), getY() + getHeight() / 2);
    }

    private String getExp(Level level) {
        return "Exp: " + level.exp + "/" + level.expToNextLevel;
    }

    private Optional<Level> getLevel() {
        Optional<Level> level = Optional.empty();
        int playerId = GameScreen.getPlayer();
        if (playerId != -1) {
            E player = E(playerId);
            if (player != null && player.hasLevel()) {
                level = Optional.of(player.getLevel());
            }
        }
        return level;
    }

    private String getLevelLabel(Level level) {
        return "Lv. " + level.level;
    }
}
