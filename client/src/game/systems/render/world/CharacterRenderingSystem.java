package game.systems.render.world;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import entity.character.Character;
import entity.character.equipment.Helmet;
import entity.character.equipment.Shield;
import entity.character.equipment.Weapon;
import entity.character.parts.Body;
import entity.character.parts.Head;
import entity.character.states.Heading;
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

@Wire(injectInherited=true)
public class CharacterRenderingSystem extends RenderingSystem {

    public static final float SHADOW_ALPHA = 0.15f;
    private static Texture shadow = new Texture(Gdx.files.local("data/ui/images/shadow22.png"));

    public CharacterRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(Character.class, WorldPos.class, Body.class, Heading.class), batch, CameraKind.WORLD);
    }

    private static float getMovementOffset(BundledAnimation bodyAnimation) {
        float animationTime = bodyAnimation.getAnimationTime();
        float interpolationTime = bodyAnimation.getAnimation().getAnimationDuration() / 2;
        return Interpolation.circle.apply(Math.min(1f, animationTime < interpolationTime ? animationTime / interpolationTime : interpolationTime / animationTime));
    }

    @Override
    protected void process(E player) {
        Pos2D currentPos = player.worldPosPos2D();
        Pos2D screenPos = Util.toScreen(currentPos);
        final Heading heading = player.getHeading();
        CharacterDrawer.createDrawer(getBatch(), player, heading, screenPos).draw();
    }

    public static class CharacterDrawer {
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

        static CharacterDrawer createDrawer(SpriteBatch batch, E player, Heading heading, Pos2D screenPos) {
            return new CharacterDrawer(batch, player, heading, screenPos);
        }

        public void draw() {
            drawShadow();
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

        private void drawShadow() {
            if (player.hasBody()) {
                final Color currentColor = new Color(batch.getColor());
                batch.setColor(currentColor.r, currentColor.g, currentColor.b, SHADOW_ALPHA);
                batch.draw(shadow, screenPos.x + (Tile.TILE_PIXEL_WIDTH - shadow.getWidth()) / 2, screenPos.y - shadow.getHeight() + 2);
                batch.setColor(currentColor);
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
            BundledAnimation bodyAnimation = this.bodyAnimation;
            return getMovementOffset(bodyAnimation);
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

    @Override
    protected Comparator<? super Entity> getComparator() {
        return (entity1, entity2) -> E(entity2).getWorldPos().y - E(entity1).getWorldPos().y;
    }

}


