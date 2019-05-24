package game.systems.render.world;

import camera.Focused;
import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import entity.character.states.Buff;
import entity.world.Dialog;
import game.utils.Fonts;

public class BuffRenderingSystem extends RenderingSystem {

    public BuffRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(Focused.class, Buff.class), batch, CameraKind.GUI);
    }

    @Override
    protected void process(E e) {
        BitmapFont font = Fonts.DIALOG_FONT;

        e.buffBuffedAtributes().forEach((attrib, time)->{
            Fonts.dialogLayout.setText(font, time.toString());
            Fonts.dialogLayout.setText(font, time.toString(), font.getColor(), 128.f, Align.center | Align.top, true);
            font.draw(getBatch(), Fonts.dialogLayout, 200, 200);
        });

    }
}
