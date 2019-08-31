package game.systems.battle;

import battle.Gems;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import entity.character.parts.Body;
import game.handlers.AnimationHandler;
import game.handlers.DescriptorHandler;
import game.systems.render.world.RenderingSystem;
import model.descriptors.BodyDescriptor;
import model.textures.BundledAnimation;
import position.Pos2D;
import position.WorldPos;
import shared.model.map.Tile;
import shared.util.Util;

@Wire
public class GemRenderingSystem extends RenderingSystem {

    private AnimationHandler animationHandler;
    private DescriptorHandler descriptorHandler;

    public GemRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(Gems.class, WorldPos.class, Body.class), batch, CameraKind.WORLD);
    }

     @Override
    protected void process(E e) {
        Gems gems = e.getGems();
        renderGem(e, Gem.RED, gems.getRed());
        renderGem(e, Gem.BLUE, gems.getBlue());
    }

    void renderGem(E e, Gem gem, int count) {
        if (count > 0) {
            Pos2D entityPos = Util.toScreen(e.worldPosPos2D());
            BundledAnimation gemAnimation = animationHandler.getTiledAnimation(gem.getAnimationId());
            TextureRegion gemTexture = gemAnimation.getGraphic();
            Body body = e.getBody();
            BodyDescriptor bodyDescriptor = descriptorHandler.getBody(body.getIndex());
            float x = entityPos.x + gem.getOffsetX();
            float y = entityPos.y + bodyDescriptor.getHeadOffsetY() + 20;
            getBatch().draw(gemTexture, x, y);
            //TODO draw count

        }
    }

    enum Gem {
        RED(0, 0, 0),
        BLUE(0, 0, (int) Tile.TILE_PIXEL_WIDTH);

        Gem(int animationId, int objId, int offsetX) {
            this.animationId = animationId;
            this.objId = objId;
            this.offsetX = offsetX;
        }

        private int animationId;
        private int objId;
        private int offsetX;

        public int getOffsetX() {
            return offsetX;
        }

        public int getAnimationId() {
            return animationId;
        }

        public int getObjId() {
            return objId;
        }
    }
}
