package game.systems.render.world;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.esotericsoftware.minlog.Log;
import entity.character.Character;
import entity.world.Ground;
import game.handlers.DescriptorHandler;
import game.managers.WorldManager;
import game.systems.camera.CameraSystem;
import game.ui.GUI;
import game.ui.Slot;
import game.utils.Colors;
import game.utils.WorldUtils;
import graphics.FX;
import model.descriptors.FXDescriptor;
import model.textures.BundledAnimation;
import position.Pos2D;
import position.WorldPos;
import shared.model.map.Tile;
import shared.util.Util;

import java.util.*;

import static com.artemis.E.E;

// TODO deduplicate code (see FXsRenderingSystem)
@Wire(injectInherited=true)
public class GroundFXsRenderingSystem extends RenderingSystem {

    private Map<Integer, Map<Integer, BundledAnimation>> fxs = new HashMap<>();
    private int srcFunc;
    private int dstFunc;

    public GroundFXsRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(FX.class, WorldPos.class, Ground.class).exclude(Character.class), batch, CameraKind.WORLD);
    }

    @Override
    protected void doBegin() {
        srcFunc = getBatch().getBlendSrcFunc();
        dstFunc = getBatch().getBlendDstFunc();
        getBatch().enableBlending();
        getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_DST_ALPHA);
    }

    @Override
    protected void doEnd() {
        getBatch().setBlendFunction(srcFunc, dstFunc);
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

    @Override
    protected void removed(int entityId) {
        super.removed(entityId);
        fxs.remove(entityId);
    }


    private void drawFXs(int entityId, Pos2D screenPos, FX fx, List<Integer> removeFXs) {
        if (fx == null || fx.fxs == null) {
            return;
        }
        fx.fxs.forEach(fxId -> {
            FXDescriptor fxDescriptor = DescriptorHandler.getFX(fxId);
            Map<Integer, BundledAnimation> anims = this.fxs.computeIfAbsent(entityId, id -> new HashMap<>());
            BundledAnimation anim = anims.computeIfAbsent(fxId, fxGraphic -> new BundledAnimation(DescriptorHandler.getGraphic(fxDescriptor.getIndexs()[0]), false));
            TextureRegion graphic = anim.getGraphic(false);
            getBatch().draw(graphic, screenPos.x + (Tile.TILE_PIXEL_WIDTH - graphic.getRegionWidth()) / 2 + fxDescriptor.getOffsetX(), screenPos.y - graphic.getRegionHeight() + fxDescriptor.getOffsetY());
            anim.setAnimationTime(anim.getAnimationTime() + getWorld().getDelta() * (anim.getFrames().size * 0.33f));
            float animationTime = anim.getAnimationTime();
            float animationDuration = anim.getAnimation().getAnimationDuration();
            Log.info(E(entityId).networkId() + "- Time: " + animationTime + " .Duration: " + animationDuration);
            if (anim.isAnimationFinished()) {
                removeFXs.add(fxId);
                anims.remove(fxId);
                Log.info("Removing entity: " + E(entityId).networkId());
                WorldManager.unregisterEntity(E(entityId).networkId());
            }
        });
    }


}
