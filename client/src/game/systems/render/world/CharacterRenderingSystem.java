package game.systems.render.world;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import entity.*;
import entity.character.Character;
import game.handlers.AnimationHandler;
import game.handlers.DescriptorHandler;
import game.systems.OrderedEntityProcessingSystem;
import game.systems.camera.CameraSystem;
import model.descriptors.BodyDescriptor;
import model.textures.BundledAnimation;
import position.Pos2D;
import position.WorldPos;
import shared.model.map.Tile;
import shared.util.Util;

import java.util.Comparator;

import static com.artemis.E.E;

@Wire
public class CharacterRenderingSystem extends OrderedEntityProcessingSystem {

    private final SpriteBatch batch;

    private CameraSystem cameraSystem;

    public CharacterRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(Character.class, WorldPos.class, Body.class, Heading.class));
        this.batch = batch;
    }

    @Override
    protected void begin() {
        cameraSystem.camera.update();
        batch.setProjectionMatrix(cameraSystem.camera.combined);
        batch.begin();
    }

    @Override
    protected void end() {
        batch.end();
    }

    @Override
    protected void process(Entity e) {
        E player = E(e);
        Pos2D currentPos = player.worldPosPos2D();
        Pos2D screenPos = Util.toScreen(currentPos);
        final Heading heading = player.getHeading();
        CharacterDrawer.createDrawer(batch, player, heading, screenPos).draw();

    }

    protected Comparator<? super Entity> getComparator() {
        return Comparator.comparingInt(entity -> E(entity).getWorldPos().y);
    }

    private static class CharacterDrawer {
        private SpriteBatch batch;
        private E player;
        private Heading heading;
        private Pos2D screenPos;

        private float headOffsetY;

        // body
        private float bodyPixelOffsetX;
        private float bodyPixelOffsetY;
        private TextureRegion bodyRegion;
        private BundledAnimation bodyAnimation;
        private float idle;

        private CharacterDrawer(SpriteBatch batch, E player, Heading heading, Pos2D screenPos) {
            this.batch = batch;
            this.player = player;
            this.heading = heading;
            this.screenPos = screenPos;
            bodyPixelOffsetX = screenPos.x;
            bodyPixelOffsetY = screenPos.y;
            calculateOffsets();
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
            BodyDescriptor bodyDescriptor = DescriptorHandler.getBodies().get(body.index);
            bodyAnimation = AnimationHandler.getBodyAnimation(body, heading.current);
            headOffsetY = bodyDescriptor.getHeadOffsetY() - getMovementOffsetY();
            bodyRegion = player.isMoving() ? bodyAnimation.getGraphic() : bodyAnimation.getGraphic(0);
            idle = !player.isMoving() ? bodyAnimation.getIdleTime() : 0;
            bodyPixelOffsetX = bodyPixelOffsetX + ((32.0f - bodyRegion.getRegionWidth()) / 2);
            bodyPixelOffsetY = screenPos.y - (bodyRegion.getRegionHeight() - 32.0f) - 32.0f;
        }

        static CharacterDrawer createDrawer(SpriteBatch batch, E player, Heading heading, Pos2D screenPos) {
            return new CharacterDrawer(batch, player, heading, screenPos);
        }

        void drawBody() {
            float offsetY = -getMovementOffsetY();
            batch.draw(bodyRegion, bodyPixelOffsetX + idle / 4, (bodyPixelOffsetY + offsetY) + idle * 1.2f, bodyRegion.getRegionWidth() - idle / 2, bodyRegion.getRegionHeight() - idle * 1.2f);
        }

        void drawHead() {
            if (player.hasHead()) {
                final Head head = player.getHead();
                BundledAnimation animation = AnimationHandler.getHeadAnimation(head, heading.current);
                if (animation != null) {
                    TextureRegion headRegion = animation.getGraphic();
                    float offsetY = headOffsetY - 4;
                    drawTexture(headRegion, bodyPixelOffsetX, bodyPixelOffsetY, 4.0f, offsetY + (idle / 2));
                }
            }
        }

        private float getMovementOffsetY() {
            float animationTime = bodyAnimation.getAnimationTime();
            float interpolationTime = bodyAnimation.getAnimation().getAnimationDuration() / 2;
            return Interpolation.circle.apply(Math.min(1f, animationTime < interpolationTime ? animationTime / interpolationTime : interpolationTime / animationTime));
        }

        void drawHelmet() {
            if (player.hasHelmet()) {
                Helmet helmet = player.getHelmet();
                BundledAnimation animation = AnimationHandler.getHelmetsAnimation(helmet, heading.current);
                if (animation != null) {
                    TextureRegion helmetRegion = animation.getGraphic();
                    float offsetY = headOffsetY - 4;
                    drawTexture(helmetRegion, bodyPixelOffsetX, bodyPixelOffsetY, 4.0f, offsetY + (idle / 2));
                }
            }
        }

        void drawWeapon() {
            if (player.hasWeapon()) {
                Weapon weapon = player.getWeapon();
                BundledAnimation animation = AnimationHandler.getWeaponAnimation(weapon, heading.current);
                if (animation != null) {
                    TextureRegion weaponRegion = player.isMoving() || player.hasAttackAnimation() ? animation.getGraphic() : animation.getGraphic(0);
                    drawTexture(weaponRegion, bodyPixelOffsetX, bodyPixelOffsetY, 0, Math.max(0, headOffsetY) + idle);
                }
            }
        }

        void drawShield() {
            if (player.hasShield()) {
                Shield shield = player.getShield();
                BundledAnimation animation = AnimationHandler.getShieldAnimation(shield, heading.current);
                if (animation != null) {
                    TextureRegion shieldRegion = player.isMoving() || player.hasAttackAnimation() ? animation.getGraphic() : animation.getGraphic(0);
                    drawTexture(shieldRegion, bodyPixelOffsetX, bodyPixelOffsetY, 0, Math.max(0, headOffsetY) + idle);
                }
            }
        }

        private void drawTexture(TextureRegion region, float x, float y, float offsetX, float offsetY) {
            if (region != null) {
                batch.draw(region, x + offsetX, (y + offsetY));
            }
        }
    }
}


