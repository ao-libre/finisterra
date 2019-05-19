package game.systems.render.world;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import entity.character.parts.Body;
import entity.world.Ground;
import game.handlers.DescriptorHandler;
import graphics.FX;
import model.descriptors.BodyDescriptor;
import model.descriptors.FXDescriptor;
import model.textures.BundledAnimation;
import position.Pos2D;
import position.WorldPos;
import shared.model.map.Tile;
import shared.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.artemis.E.E;

@Wire(injectInherited=true)
public class FXsRenderingSystem extends RenderingSystem {

    private Map<Integer, Map<Integer, BundledAnimation>> fxs = new HashMap<>();
    private int srcFunc;
    private int dstFunc;

    public FXsRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(FX.class, WorldPos.class).exclude(Ground.class), batch, CameraKind.WORLD);
    }
    @Override
    protected void doBegin() {
        srcFunc = getBatch().getBlendSrcFunc();
        dstFunc = getBatch().getBlendDstFunc();
        getBatch().enableBlending();
        getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    protected void doEnd() {
        getBatch().setBlendFunction(srcFunc, dstFunc);
    }


    @Override
    protected void removed(int entityId) {
        super.removed(entityId);
        fxs.remove(entityId);
    }

    @Override
    protected void process(E entity) {
        Pos2D screenPos = Util.toScreen(entity.worldPosPos2D());
        final FX fx = entity.getFX();
        if (fx.fxs.isEmpty()) {
            return;
        }
        List<Integer> removeFXs = new ArrayList<>();
        drawFXs(entity.id(), screenPos, fx, removeFXs);
        removeFXs.forEach(remove -> fx.removeFx(remove));
        if (fx.fxs.isEmpty()) {
            fxs.remove(entity.id());
        }
    }

    private void drawFXs(int entityId, Pos2D screenPos, FX fx, List<Integer> removeFXs) {
        if (fx == null || fx.fxs == null) {
            return;
        }
        fx.fxs.forEach(fxId -> {
            FXDescriptor fxDescriptor = DescriptorHandler.getFX(fxId);
            Map<Integer, BundledAnimation> anims = this.fxs.computeIfAbsent(entityId, id -> new HashMap<>());
            int bodyOffset = getBodyOffset(entityId);
            BundledAnimation anim = anims.computeIfAbsent(fxId, fxGraphic -> new BundledAnimation(DescriptorHandler.getGraphic(fxDescriptor.getIndexs()[0]), false));
            TextureRegion graphic = anim.getGraphic(false);
            getBatch().draw(graphic, screenPos.x + (Tile.TILE_PIXEL_WIDTH - graphic.getRegionWidth()) / 2 + fxDescriptor.getOffsetX(), screenPos.y - graphic.getRegionHeight() + fxDescriptor.getOffsetY() + bodyOffset);
            anim.setAnimationTime(anim.getAnimationTime() + getWorld().getDelta() * (anim.getFrames().size * 0.33f));
            if (anim.isAnimationFinished()) {
                removeFXs.add(fxId);
                anims.remove(fxId);
            }
        });
    }

    private int getBodyOffset(int entityId) {
        int headOffsetY = 0;
        if (E(entityId).hasBody()) {
            final Body body = E(entityId).getBody();
            BodyDescriptor bodyDescriptor = DescriptorHandler.getBodies().get(body.index);
            headOffsetY = Math.max(0, bodyDescriptor.getHeadOffsetY());
        }
        return headOffsetY;
    }

}
