package design.systems;

import camera.Focused;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import game.systems.render.world.CharacterRenderingSystem;
import game.systems.render.world.RenderingSystem;
import position.WorldPos;

import java.util.Optional;

@Wire(injectInherited = true)
public class PreviewRenderingSystem extends RenderingSystem {
    private CharacterRenderingSystem renderingSystem;

    public PreviewRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(Focused.class), batch, CameraKind.WORLD);
    }

    @Override
    protected void process(E e) {
        renderingSystem.drawPlayer(e, Optional.of(new WorldPos(20,11,1)));
    }
}
