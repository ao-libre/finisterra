package game.systems.render.world;

import camera.Focused;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import entity.character.Character;
import game.systems.camera.CameraSystem;
import game.utils.Colors;
import game.utils.Fonts;
import model.textures.TextureUtils;

import static com.artemis.E.E;

@Wire
public class CharacterStatusRenderingSystem extends IteratingSystem {

    public static final int BAR_HEIGHT = 10;
    public static final int BORDER = 2;
    public static final float ALPHA = 0.7f;
    public static final int OFFSET_Y = 5;
    private static final int BAR_WIDTH = 400;
    public static float OFFSET_X = (Gdx.graphics.getWidth() + BAR_WIDTH) / 2;
    private final SpriteBatch batch;
    private CameraSystem cameraSystem;

    public CharacterStatusRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(Character.class, Focused.class));
        this.batch = batch;
    }

    @Override
    protected void process(int entity) {
        cameraSystem.guiCamera.update();
        batch.setProjectionMatrix(cameraSystem.guiCamera.combined);
        batch.begin();
        OFFSET_X = (cameraSystem.guiCamera.viewportWidth / 2) - BAR_WIDTH / 2;
        drawExp(E(entity));
        drawHealth(E(entity));
        drawMana(E(entity));
        batch.end();
    }

    private void drawExp(E player) {
        int maxExp = player.getExp().exp;
        int minExp = player.getElv().elv;
        drawBar(maxExp, minExp, (int) OFFSET_X, 0, Colors.EXP.cpy(), OFFSET_Y);
    }

    private void drawMana(E player) {
        int maxMana = player.getMana().max;
        int minMana = player.getMana().min;
        drawBar(maxMana, minMana, (int) OFFSET_X, OFFSET_Y + BAR_HEIGHT + 1, Colors.MANA.cpy(), BAR_HEIGHT);
        drawText(maxMana, minMana, (int) OFFSET_X, OFFSET_Y + BAR_HEIGHT + 1);
    }

    private void drawHealth(E player) {
        int maxHealth = player.getHealth().max;
        int health = player.getHealth().min;
        drawBar(maxHealth, health, (int) OFFSET_X, OFFSET_Y, Colors.HEALTH.cpy(), BAR_HEIGHT);
        drawText(maxHealth, health, (int) OFFSET_X, OFFSET_Y);
    }

    private void drawBar(int max, int value, int offsetX, int offsetY, Color barColor, int barHeight) {
        //background
        Color black = Color.BLACK.cpy();
        batch.setColor(black.r, black.g, black.b, ALPHA);
        batch.draw(TextureUtils.white, offsetX, offsetY, BAR_WIDTH + BORDER, barHeight);

        //color
        batch.setColor(barColor.r, barColor.g, barColor.b, ALPHA);
        batch.draw(TextureUtils.white, offsetX + BORDER / 2, offsetY + BORDER / 2, ((float) value / max) * BAR_WIDTH, barHeight - BORDER);

    }

    private void drawText(int max, int value, int offsetX, int offsetY) {
        //text
        batch.setColor(Color.WHITE.cpy());
        Fonts.layout.setText(Fonts.WHITE_FONT, value + "/" + max);
        final float fontX = offsetX + (BAR_WIDTH + BORDER - Fonts.layout.width) / 2;
        final float fontY = offsetY + (BAR_HEIGHT + Fonts.layout.height) / 2;
        Fonts.WHITE_FONT.draw(batch, Fonts.layout, fontX, fontY);
    }

}


