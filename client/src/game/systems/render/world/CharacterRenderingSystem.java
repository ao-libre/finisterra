package game.systems.render.world;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.Color;
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
import game.systems.render.BatchRenderingSystem;
import game.systems.render.BatchTask;
import game.utils.Pos2D;
import model.descriptors.BodyDescriptor;
import model.textures.AOTexture;
import model.textures.BundledAnimation;
import position.WorldPos;
import shared.model.map.Tile;

import java.util.Comparator;
import java.util.Optional;

import static com.artemis.E.E;
import static game.systems.render.world.CharacterRenderingSystem.CharacterDrawer.createDrawer;

@Wire(injectInherited = true)
public class CharacterRenderingSystem extends RenderingSystem {

    private static final Aspect.Builder CHAR_ASPECT = Aspect.all(WorldPos.class, Body.class, Heading.class);
    private DescriptorHandler descriptorHandler;
    private AnimationHandler animationHandler;
    private BatchRenderingSystem batchRenderingSystem;

    public CharacterRenderingSystem() {
        super(CHAR_ASPECT);
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
        Pos2D currentPos = Pos2D.get(pos, player.getWorldPosOffsets());
        Pos2D screenPos = currentPos.toScreen();
        createDrawer(batchRenderingSystem, player, screenPos, descriptorHandler, animationHandler).draw();
    }

    @Override
    protected Comparator<? super Entity> getComparator() {
        return (entity1, entity2) -> E(entity2).getWorldPos().y - E(entity1).getWorldPos().y;
    }

    public static class CharacterDrawer {
        static final float FACTOR = 1.2f;
        private BatchRenderingSystem batchRenderingSystem;
        private final E player;
        private final Heading heading;
        private final Pos2D screenPos;
        private final DescriptorHandler descriptorHandler;
        private final AnimationHandler animationHandler;
        private boolean shouldFlip;
        private float headOffsetY;
        // body
        private float bodyPixelOffsetX;
        private float bodyPixelOffsetY;
        private TextureRegion bodyRegion;
        private BundledAnimation bodyAnimation;
        private float idle;

        private CharacterDrawer(BatchRenderingSystem batchRenderingSystem, E player, Pos2D screenPos, DescriptorHandler descriptorHandler, AnimationHandler animationHandler) {
            this.batchRenderingSystem = batchRenderingSystem;
            this.player = player;
            this.heading = player.getHeading();
            this.screenPos = screenPos;
            bodyPixelOffsetX = screenPos.x;
            bodyPixelOffsetY = screenPos.y;
            this.descriptorHandler = descriptorHandler;
            this.animationHandler = animationHandler;
            calculateOffsets();
        }

        public static CharacterDrawer createDrawer(BatchRenderingSystem batchRenderingSystem, E player, Pos2D screenPos, DescriptorHandler descriptorHandler, AnimationHandler animationHandler) {
            return new CharacterDrawer(batchRenderingSystem, player, screenPos, descriptorHandler, animationHandler);
        }

        public static CharacterDrawer createDrawer(BatchRenderingSystem batchRenderingSystem, E player, Pos2D screenPos, DescriptorHandler descriptorHandler, AnimationHandler animationHandler, boolean shouldFlip) {
            CharacterDrawer characterDrawer = new CharacterDrawer(batchRenderingSystem, player, screenPos, descriptorHandler, animationHandler);
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
            float x = bodyPixelOffsetX + idle / 4;
            float y = (bodyPixelOffsetY + offsetY) + idle * FACTOR;
            float width = bodyRegion.getRegionWidth() - idle / 2;
            float height = bodyRegion.getRegionHeight() - idle * FACTOR;

            batchRenderingSystem.addTask((batch) ->
                    {
                        if (animate()) {
                            TextureRegion previousBodyRegion = bodyAnimation.getPreviousGraphic();
                            if (previousBodyRegion != null) {
                                if (previousBodyRegion.isFlipY() && shouldFlip) {
                                    previousBodyRegion.flip(false, true);
                                }
                                Color color = batch.getColor();
                                float previusTransparency = color.a;
                                color.a = bodyAnimation.getPreviousFrameTransparency();
                                batch.setColor(color);
                                batch.draw(previousBodyRegion, x, y, width, height);
                                color.a = previusTransparency;
                                batch.setColor(color);
                            }
                        }
                        if (bodyRegion != null) {
                            if (bodyRegion.isFlipY() && shouldFlip) {
                                bodyRegion.flip(false, true);
                            }
                            batch.draw(bodyRegion, x, y, width, height);
                        }
                    }
            );
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
            BundledAnimation bodyAnimationOffsetY = this.bodyAnimation;
            return getMovementOffset(bodyAnimationOffsetY);
        }

        void drawHelmet() {
            if (player.hasHelmet()) {
                Helmet helmet = player.getHelmet();
                BundledAnimation animation = animationHandler.getHelmetsAnimation(helmet, heading.current);
                float offsetY = headOffsetY - 4 * SCALE;
                float offsetX = 4.0f * SCALE;
                draw(animation, this.bodyPixelOffsetX, this.bodyPixelOffsetY, offsetX, offsetY);
            }
        }

        void drawWeapon() {
            if (player.hasWeapon()) {
                Weapon weapon = player.getWeapon();
                BundledAnimation animation = animationHandler.getWeaponAnimation(weapon, heading.current);
                draw(animation, this.bodyPixelOffsetX, this.bodyPixelOffsetY, 0, Math.max(0, headOffsetY) + idle);
            }
        }


        void drawShield() {
            if (player.hasShield()) {
                Shield shield = player.getShield();
                BundledAnimation animation = animationHandler.getShieldAnimation(shield, heading.current);
                draw(animation, this.bodyPixelOffsetX, this.bodyPixelOffsetY, 0, Math.max(0, headOffsetY) + idle);
            }
        }

        boolean animate() {
            return player.isMoving() || player.hasAttackAnimation();
        }

        private void draw(BundledAnimation animation, float x, float y, float offsetX, float offsetY) {
            if (animation != null) {
                if (animate()) {
                    drawTexture(animation.getPreviousGraphic(), x, y, offsetX, offsetY, animation.getPreviousFrameTransparency());
                    drawTexture(animation.getGraphic(), x, y, offsetX, offsetY);
                } else {
                    drawTexture(animation.getGraphic(0), x, y, offsetX, offsetY);
                }
            }
        }

        private void drawTexture(TextureRegion region, float x, float y, float offsetX, float offsetY) {
            drawTexture(region, x, y, offsetX, offsetY, 1);
        }

        private void drawTexture(TextureRegion region, float x, float y, float offsetX, float offsetY, float transparency) {
            if (region != null) {
                if (region.isFlipY() && shouldFlip) {
                    region.flip(false, true);
                }
                float x1 = x + offsetX;
                float y1 = y + offsetY * (shouldFlip ? -1 : 1);
                BatchTask drawTexture = (batch) ->
                {
                    Color color = batch.getColor();
                    float previusTransparency = color.a;
                    color.a = transparency;
                    batch.setColor(color);
                    batch.draw(region, x1, y1);
                    color.a = previusTransparency;
                    batch.setColor(color);
                };
                batchRenderingSystem.addTask(drawTexture);
            }
        }
    }

}
