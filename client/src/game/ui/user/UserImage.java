package game.ui.user;

import com.artemis.E;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import component.entity.character.status.Level;
import game.utils.Skins;
import model.textures.RadialProgress;
import model.textures.RadialSprite;

import java.util.Optional;

public class UserImage extends ImageButton {

    private final Label lvlLabel;
    private final RadialProgress radialProgress;
    private final RadialSprite radialSprite;
    private E e;

    UserImage(E e) {
        super(Skins.COMODORE_SKIN, "big-disc");
        this.e = e;
        lvlLabel = new Label("", Skins.COMODORE_SKIN, "title-no-background");
        lvlLabel.setAlignment(Align.center);
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
        float angle = (360 * percent);
        radialSprite.setAngle(angle);
        radialSprite.setScale(1.5f, 1.5f);
        radialSprite.draw(batch, getX() + 15, getY() + 15, getWidth() - 16, getHeight() - 15);

    }

    private float getPercent(Level level) {
        return (float) level.exp / (float) level.expToNextLevel;
    }

    private void drawLevel(Batch batch, Level level) {
        lvlLabel.setText(getLevelLabel(level));
        lvlLabel.setPosition(getX(), getY() + (getHeight() - lvlLabel.getHeight()) / 2);
        lvlLabel.setWidth(getWidth());
        lvlLabel.draw(batch, 1);
    }

    private String getExp(Level level) {
        return "Exp: " + level.exp + "/" + level.expToNextLevel;
    }

    private Optional<Level> getLevel() {
        Optional<Level> level = Optional.empty();
        if (e != null && e.hasLevel()) {
            level = Optional.of(e.getLevel());
        }

        return level;
    }

    private String getLevelLabel(Level level) {
        return "Lv. " + level.level;
    }
}
