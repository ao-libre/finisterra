package game.systems.render.world;

import camera.Focused;
import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import entity.character.states.Buff;
import entity.world.Dialog;
import game.screens.GameScreen;
import game.systems.network.TimeSync;
import game.utils.Fonts;

public class BuffRenderingSystem extends RenderingSystem {

    public BuffRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(Focused.class, Buff.class), batch, CameraKind.GUI);
    }

    @Override
    protected void process(E e) {
        BitmapFont font = Fonts.DIALOG_FONT;

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
        }

    }
}
