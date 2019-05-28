package game.systems.render.world;

import camera.Focused;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import entity.character.attributes.Attribute;
import entity.character.states.Buff;
import entity.world.Dialog;
import game.screens.GameScreen;
import game.systems.OrderedEntityProcessingSystem;
import game.systems.camera.CameraSystem;
import game.systems.network.TimeSync;
import game.utils.Fonts;
import model.textures.TextureUtils;
import position.WorldPos;

import java.util.Comparator;

public class BuffRenderingSystem extends OrderedEntityProcessingSystem {

    public static final float ALPHA = 0.5f;
    private static final int BORDER = 6;
    private SpriteBatch batch;
    private CameraSystem cameraSystem;
    int yOffset = 100;

    public BuffRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(Focused.class, Buff.class));
        this.batch = batch;
    }

    @Override
    protected void begin() {
        cameraSystem.guiCamera.update();
        batch.setProjectionMatrix(cameraSystem.guiCamera.combined);
        batch.begin();
    }

    @Override
    protected void end() {
        batch.end();
    }

        /*BitmapFont font = Fonts.DIALOG_FONT;

        TimeSync timeSyncSystem = GameScreen.getWorld().getSystem(TimeSync.class);
        long rtt = timeSyncSystem.getRtt();
        long timeOffset = timeSyncSystem.getTimeOffset();

        if (e.buffBuffedAtributes().isEmpty())
        {
            e.buffBuffedAtributes().forEach((attrib, time)->{
                e.buffBuffedAtributes().put(attrib, e.buffBuffedAtributes().get(attrib) - getWorld().getDelta());
                Fonts.dialogLayout.setText(font, time.toString());
                Fonts.dialogLayout.setText(font, time.toString(), font.getColor(), 128.f, Align.center | Align.top, true);
                font.draw(getBatch(), Fonts.dialogLayout, 200, 200);
                if (time <= 0.f) e.buffBuffedAtributes().remove(attrib);
            });
        }
        else
        {
            e.removeBuff();
        }*/



    @Override
    protected void process(Entity e) {
        E player = E.E(e);

        player.buffBuffedAtributes().forEach((attrib, time)->{

            player.buffBuffedAtributes().put(attrib, player.buffBuffedAtributes().get(attrib) - getWorld().getDelta());

            drawCoordinates(50, yOffset, time, attrib);

            yOffset += 50;

            if (time <= 0.f) player.buffBuffedAtributes().remove(attrib);

        });

        yOffset = 100;

    }

    private void drawCoordinates(int offsetX, int offsetY, Float number, Attribute attrib) {
        String worldPosString = "[" + attrib.getClass().getSimpleName() + ":" + attrib.getCurrentValue() + " Time Left:" + number.intValue() + "]";
        BitmapFont font = Fonts.CONSOLE_FONT;
        Fonts.layout.setText(font, worldPosString);
        float fontX = cameraSystem.guiCamera.viewportWidth - Fonts.layout.width - offsetX;
        float fontY = cameraSystem.guiCamera.viewportHeight - offsetY;
        //background
        Color oldColor = batch.getColor();
        Color black = Color.BLACK.cpy();
        batch.setColor(black.r, black.g, black.b, ALPHA);
        batch.draw(TextureUtils.white, fontX - (BORDER >> 1), fontY - (BORDER >> 1), Fonts.layout.width + BORDER, Fonts.layout.height + BORDER);
        //text
        batch.setColor(Color.WHITE.cpy());
        font.draw(batch, Fonts.layout, fontX, fontY + Fonts.layout.height);
        batch.setColor(oldColor);
    }

    @Override
    protected Comparator<? super Entity> getComparator() {
        return Comparator.comparingInt(entity -> E.E(entity).getWorldPos().y);
    }
}
