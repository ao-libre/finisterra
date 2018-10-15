package ar.com.tamborindeguy.client.systems.render.world;

import ar.com.tamborindeguy.client.handlers.AnimationHandler;
import ar.com.tamborindeguy.client.handlers.DescriptorHandler;
import ar.com.tamborindeguy.client.systems.OrderedEntityProcessingSystem;
import ar.com.tamborindeguy.client.systems.camera.CameraSystem;
import ar.com.tamborindeguy.model.descriptors.BodyDescriptor;
import ar.com.tamborindeguy.model.textures.BundledAnimation;
import ar.com.tamborindeguy.util.Util;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import entity.*;
import entity.character.Character;
import position.Pos2D;

import java.util.Comparator;

import static com.artemis.E.E;

@Wire
public class CharacterRenderingSystem extends OrderedEntityProcessingSystem {

    private final SpriteBatch batch;

    private CameraSystem cameraSystem;

    public CharacterRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(Character.class, Pos2D.class, Body.class, Heading.class));
        this.batch = batch;
    }

    @Override
    protected void process(Entity e) {
        cameraSystem.camera.update();
        batch.setProjectionMatrix(cameraSystem.camera.combined);
        batch.enableBlending();
        batch.begin();

        E player = E(e);
        Pos2D currentPos = player.getPos2D();
        Pos2D screenPos = Util.toScreen(currentPos);
        final Heading heading = player.getHeading();

        CharacterDrawer.createDrawer(batch, player, heading, screenPos).draw();

        batch.end();
    }

    protected Comparator<? super Entity> getComparator() {
        return Comparator.comparingInt(entity -> E(entity).getWorldPos().y);
    }

    private static class CharacterDrawer {
        private SpriteBatch batch;
        private E player;
        private Heading heading;
        private Pos2D screenPos;

        private int headOffsetY;

        // body
        private float bodyPixelOffsetX;
        private float bodyPixelOffsetY;
        private TextureRegion bodyRegion;
        private BundledAnimation bodyAnimation;

        private CharacterDrawer(SpriteBatch batch, E player, Heading heading, Pos2D screenPos) {
            this.batch = batch;
            this.player = player;
            this.heading = heading;
            this.screenPos = screenPos;
            bodyPixelOffsetX = screenPos.x - 32.0f;
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
            headOffsetY = bodyDescriptor.getHeadOffsetY();
            bodyAnimation = AnimationHandler.getBodyAnimation(body, heading.current);
            bodyRegion = player.isMoving() ? bodyAnimation.getGraphic() : bodyAnimation.getGraphic(0);
            bodyPixelOffsetX = bodyPixelOffsetX + ((32.0f - bodyRegion.getRegionWidth()) / 2);
            bodyPixelOffsetY = screenPos.y - (bodyRegion.getRegionHeight() - 32.0f) - 32.0f;
        }

        static CharacterDrawer createDrawer(SpriteBatch batch, E player, Heading heading, Pos2D screenPos) {
            return new CharacterDrawer(batch, player, heading, screenPos);
        }


        void drawBody() {
            drawTexture(bodyRegion, bodyPixelOffsetX, bodyPixelOffsetY, 0, -(!player.isMoving() && player.hasAttackAnimation() ? getExtraPixel() : 0)); // why - 32 - 32 ?
        }

        void drawHead() {
            if (player.hasHead()) {
                final Head head = player.getHead();
                BundledAnimation animation = AnimationHandler.getHeadAnimation(head, heading.current);
                if (animation != null) {
                    TextureRegion headRegion = animation.getGraphic();
                    int offsetY = headOffsetY - 4 - getExtraPixel();
                    drawTexture(headRegion, bodyPixelOffsetX, bodyPixelOffsetY, 4.0f, offsetY);
                }
            }
        }

        private int getExtraPixel() {
            int currentFrameIndex = bodyAnimation.getCurrentFrameIndex();
            return currentFrameIndex == 2 || currentFrameIndex == 3 ? 1 : 0;
        }

        void drawHelmet() {
            if (player.hasHelmet()) {
                Helmet helmet = player.getHelmet();
                BundledAnimation animation = AnimationHandler.getHelmetsAnimation(helmet, heading.current);
                if (animation != null) {
                    TextureRegion helmetRegion = animation.getGraphic();
                    int offsetY = headOffsetY - 4 - getExtraPixel();
                    drawTexture(helmetRegion, bodyPixelOffsetX, bodyPixelOffsetY, 4.0f, offsetY);
                }
            }
        }

        void drawWeapon() {
            if (player.hasWeapon()) {
                Weapon weapon = player.getWeapon();
                BundledAnimation animation = AnimationHandler.getWeaponAnimation(weapon, heading.current);
                if (animation != null) {
                    TextureRegion weaponRegion = player.isMoving() || player.hasAttackAnimation() ? animation.getGraphic() : animation.getGraphic(0);
                    drawTexture(weaponRegion, bodyPixelOffsetX, bodyPixelOffsetY, 0, headOffsetY);
                }
            }
        }

        void drawShield() {
            if (player.hasShield()) {
                Shield shield = player.getShield();
                BundledAnimation animation = AnimationHandler.getShieldAnimation(shield, heading.current);
                if (animation != null) {
                    TextureRegion shieldRegion = player.isMoving() || player.hasAttackAnimation() ? animation.getGraphic() : animation.getGraphic(0);
                    drawTexture(shieldRegion, bodyPixelOffsetX, bodyPixelOffsetY, 0, headOffsetY);
                }
            }
        }

        private void drawTexture(TextureRegion region, float x, float y, float offsetX, float offsetY) {
            if (region != null) {
                batch.draw(region, x + offsetX, y + offsetY);
            }
        }
    }
}


