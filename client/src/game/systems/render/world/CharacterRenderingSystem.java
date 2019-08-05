package game.systems.render.world;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import entity.character.equipment.Helmet;
import entity.character.equipment.Shield;
import entity.character.equipment.Weapon;
import entity.character.parts.Body;
import entity.character.parts.Head;
import entity.character.states.Heading;
import game.handlers.AnimationHandler;
import game.handlers.DescriptorHandler;
import model.descriptors.BodyDescriptor;
import model.textures.AOTexture;
import model.textures.BundledAnimation;
import position.Pos2D;
import position.WorldPos;
import shared.model.map.Tile;
import shared.util.Util;

import java.util.Comparator;
import java.util.Optional;

import static com.artemis.E.E;
import static game.systems.render.world.CharacterRenderingSystem.CharacterDrawer.createDrawer;

@Wire(injectInherited = true)
public class CharacterRenderingSystem extends RenderingSystem {

    private static final Aspect.Builder CHAR_ASPECT = Aspect.all(WorldPos.class, Body.class, Heading.class);
    private DescriptorHandler descriptorHandler;
    private AnimationHandler animationHandler;

    public CharacterRenderingSystem(SpriteBatch batch) {
        super(CHAR_ASPECT, batch, CameraKind.WORLD);
    }

    private static float getMovementOffset(BundledAnimation bodyAnimation) {
        float animationTime = bodyAnimation.getAnimationTime();
        float interpolationTime = bodyAnimation.getAnimation().getAnimationDuration() / 2;
        return Interpolation.circle.apply(Math.min(1f, animationTime < interpolationTime ? animationTime / interpolationTime : interpolationTime / animationTime));
    }

    @Override
    protected void process(E player) {
    }

    public Aspect.Builder getAspect() {
        return CHAR_ASPECT;
    }

    public void drawPlayer(E player, Optional<WorldPos> forcedPos) {
        WorldPos pos = forcedPos.orElse(player.getWorldPos());
        Pos2D currentPos = pos.getPos2D();
        Pos2D screenPos = Util.toScreen(currentPos);
        createDrawer(getBatch(), player, screenPos, descriptorHandler, animationHandler).draw();
    }

    @Override
    protected Comparator<? super Entity> getComparator() {
        return (entity1, entity2) -> E(entity2).getWorldPos().y - E(entity1).getWorldPos().y;
    }

    public static class CharacterDrawer {
        private boolean shouldFlip;
        private Batch batch;
        private E player;
        private Heading heading;
        private Pos2D screenPos;
        private float headOffsetY;

        // body
        private float bodyPixelOffsetX;
        private float bodyPixelOffsetY;
        private DescriptorHandler descriptorHandler;
        private AnimationHandler animationHandler;
        private TextureRegion bodyRegion;
        private BundledAnimation bodyAnimation;
        private float idle;

        private CharacterDrawer(Batch batch, E player, Pos2D screenPos, DescriptorHandler descriptorHandler, AnimationHandler animationHandler) {
            this.batch = batch;
            this.player = player;
            this.heading = player.getHeading();
            this.screenPos = screenPos;
            bodyPixelOffsetX = screenPos.x;
            bodyPixelOffsetY = screenPos.y;
            this.descriptorHandler = descriptorHandler;
            this.animationHandler = animationHandler;
            calculateOffsets();
        }

        public static CharacterDrawer createDrawer(Batch batch, E player, Pos2D screenPos, DescriptorHandler descriptorHandler, AnimationHandler animationHandler) {
            return new CharacterDrawer(batch, player, screenPos, descriptorHandler, animationHandler);
        }

        public static CharacterDrawer createDrawer(Batch batch, E player, Pos2D screenPos, DescriptorHandler descriptorHandler, AnimationHandler animationHandler, boolean shouldFlip) {
            CharacterDrawer characterDrawer = new CharacterDrawer(batch, player, screenPos, descriptorHandler, animationHandler);
            characterDrawer.shouldFlip = shouldFlip;
            return characterDrawer;
        }

        public void draw() {
            int current = player.getHeading().current;
            switch (current) {
                case Heading.HEADING_NORTH:
                    drawWeapon();
                    drawShield();
                    drawBody();
                    drawHead();
                    drawHelmet();
                    break;
                case Heading.HEADING_SOUTH:
                    drawBody();
                    drawHead();
                    drawHelmet();
                    drawWeapon();
                    drawShield();
                    break;
                case Heading.HEADING_EAST:
                    drawShield();
                    drawBody();
                    drawHead();
                    drawHelmet();
                    drawWeapon();
                    break;
                case Heading.HEADING_WEST:
                    drawWeapon();
                    drawBody();
                    drawHead();
                    drawHelmet();
                    drawShield();
                    break;
            }
        }

        private void calculateOffsets() {
            final Body body = player.getBody();
            BodyDescriptor bodyDescriptor = descriptorHandler.getBody(body.index);
            bodyAnimation = animationHandler.getBodyAnimation(body, heading.current);

            headOffsetY = bodyDescriptor.getHeadOffsetY() - getMovementOffsetY();
            headOffsetY *= SCALE;

            bodyRegion = player.isMoving() ? bodyAnimation.getGraphic() : bodyAnimation.getGraphic(0);
            idle = !player.isMoving() ? bodyAnimation.getIdleTime() : 0;
            idle *= SCALE;
            bodyPixelOffsetX = bodyPixelOffsetX + ((Tile.TILE_PIXEL_WIDTH - bodyRegion.getRegionWidth()) / 2);
            bodyPixelOffsetY = screenPos.y - (bodyRegion.getRegionHeight() - Tile.TILE_PIXEL_HEIGHT) - Tile.TILE_PIXEL_HEIGHT;
        }

        void drawBody() {
            float offsetY = -getMovementOffsetY() * SCALE;
            if (bodyRegion.isFlipY() && shouldFlip) {
                bodyRegion.flip(false, true);
            }
            batch.draw(bodyRegion, bodyPixelOffsetX + idle / 4, (bodyPixelOffsetY + offsetY) + idle * 1.2f, bodyRegion.getRegionWidth() - idle / 2, bodyRegion.getRegionHeight() - idle * 1.2f);
        }

        void drawHead() {
            if (player.hasHead()) {
                final Head head = player.getHead();
                AOTexture headTexture = animationHandler.getHeadAnimation(head, heading.current);
                if (headTexture != null) {
                    TextureRegion headRegion = headTexture.getTexture();
                    float offsetY = headOffsetY - (shouldFlip ? -1 : 1) * 4 * SCALE;
                    drawTexture(headRegion, bodyPixelOffsetX, bodyPixelOffsetY, 4.0f * SCALE, offsetY + (idle / 2));
                }
            }
        }

        private float getMovementOffsetY() {
            BundledAnimation bodyAnimation = this.bodyAnimation;
            return getMovementOffset(bodyAnimation);
        }

        void drawHelmet() {
            if (player.hasHelmet()) {
                Helmet helmet = player.getHelmet();
                BundledAnimation animation = animationHandler.getHelmetsAnimation(helmet, heading.current);
                if (animation != null) {
                    TextureRegion helmetRegion = animation.getGraphic();
                    float offsetY = headOffsetY - 4 * SCALE;
                    drawTexture(helmetRegion, bodyPixelOffsetX, bodyPixelOffsetY, 4.0f * SCALE, offsetY + (idle / 2));
                }
            }
        }

        void drawWeapon() {
            if (player.hasWeapon()) {
                Weapon weapon = player.getWeapon();
                BundledAnimation animation = animationHandler.getWeaponAnimation(weapon, heading.current);
                if (animation != null) {
                    TextureRegion weaponRegion = player.isMoving() || player.hasAttackAnimation() ? animation.getGraphic() : animation.getGraphic(0);
                    drawTexture(weaponRegion, bodyPixelOffsetX, bodyPixelOffsetY, 0, Math.max(0, headOffsetY) + idle);
                }
            }
        }

        void drawShield() {
            if (player.hasShield()) {
                Shield shield = player.getShield();
                BundledAnimation animation = animationHandler.getShieldAnimation(shield, heading.current);
                if (animation != null) {
                    TextureRegion shieldRegion = player.isMoving() || player.hasAttackAnimation() ? animation.getGraphic() : animation.getGraphic(0);
                    drawTexture(shieldRegion, bodyPixelOffsetX, bodyPixelOffsetY, 0, Math.max(0, headOffsetY) + idle);
                }
            }
        }

        private void drawTexture(TextureRegion region, float x, float y, float offsetX, float offsetY) {
            if (region != null) {
                if (region.isFlipY() && shouldFlip) {
                    region.flip(false, true);
                }
                batch.draw(region, x + offsetX, (y + offsetY * (shouldFlip ? -1 : 1)));
            }
        }
    }

}
