package game.systems.render.world;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import component.entity.character.equipment.Helmet;
import component.entity.character.equipment.Shield;
import component.entity.character.equipment.Weapon;
import component.entity.character.parts.Body;
import component.entity.character.parts.Head;
import component.entity.character.states.Heading;
import game.systems.resources.AnimationsSystem;
import game.systems.resources.DescriptorsSystem;
import game.systems.render.BatchRenderingSystem;
import game.systems.render.BatchTask;
import game.utils.Pos2D;
import model.descriptors.BodyDescriptor;
import model.textures.AOTexture;
import model.textures.BundledAnimation;
import component.position.WorldPos;
import shared.model.map.Tile;

import java.util.Comparator;
import java.util.Optional;

import static com.artemis.E.E;
import static game.systems.render.world.CharacterRenderingSystem.CharacterDrawer.createDrawer;

/**
 * Clase para el renderizado de personajes
 */
@Wire(injectInherited = true)
public class CharacterRenderingSystem extends RenderingSystem {

    private static final Aspect.Builder CHAR_ASPECT = Aspect.all(WorldPos.class, Body.class, Heading.class);
    private DescriptorsSystem descriptorsSystem;
    private AnimationsSystem animationsSystem;
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
        createDrawer(batchRenderingSystem, player, screenPos, descriptorsSystem, animationsSystem).draw();
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
        private final DescriptorsSystem descriptorsSystem;
        private final AnimationsSystem animationsSystem;
        private boolean shouldFlip;
        private float headOffsetY;
        // body
        private float bodyPixelOffsetX;
        private float bodyPixelOffsetY;
        private TextureRegion bodyRegion;
        private BundledAnimation bodyAnimation;
        private float idle;

        private CharacterDrawer(BatchRenderingSystem batchRenderingSystem, E player, Pos2D screenPos, DescriptorsSystem descriptorsSystem, AnimationsSystem animationsSystem) {
            this.batchRenderingSystem = batchRenderingSystem;
            this.player = player;
            this.heading = player.getHeading();
            this.screenPos = screenPos;
            bodyPixelOffsetX = screenPos.x;
            bodyPixelOffsetY = screenPos.y;
            this.descriptorsSystem = descriptorsSystem;
            this.animationsSystem = animationsSystem;
            calculateOffsets();
        }

        public static CharacterDrawer createDrawer(BatchRenderingSystem batchRenderingSystem, E player, Pos2D screenPos, DescriptorsSystem descriptorsSystem, AnimationsSystem animationsSystem) {
            return new CharacterDrawer(batchRenderingSystem, player, screenPos, descriptorsSystem, animationsSystem);
        }

        public static CharacterDrawer createDrawer(BatchRenderingSystem batchRenderingSystem, E player, Pos2D screenPos, DescriptorsSystem descriptorsSystem, AnimationsSystem animationsSystem, boolean shouldFlip) {
            CharacterDrawer characterDrawer = new CharacterDrawer(batchRenderingSystem, player, screenPos, descriptorsSystem, animationsSystem);
            characterDrawer.shouldFlip = shouldFlip;
            return characterDrawer;
        }

        /**
         * Dibuja los cuerpos en la direccion en la que se encuentren
         */
        public void draw() {
            int current = player.getHeading().current;
            switch (current) { //¿A que direccion esta mirando?
                case Heading.HEADING_NORTH: //Norte
                    drawWeapon();
                    drawShield();
                    drawBody();
                    drawHead();
                    drawHelmet();
                    break;
                case Heading.HEADING_SOUTH: //Sur
                    drawBody();
                    drawHead();
                    drawHelmet();
                    drawWeapon();
                    drawShield();
                    break;
                case Heading.HEADING_EAST: //Este
                    drawShield();
                    drawBody();
                    drawHead();
                    drawHelmet();
                    drawWeapon();
                    break;
                case Heading.HEADING_WEST: //Oeste
                    drawWeapon();
                    drawBody();
                    drawHead();
                    drawHelmet();
                    drawShield();
                    break;
            }
        }

        /**
         * Calcula la posicion del personaje en la pantalla
         */
        private void calculateOffsets() {
            final Body body = player.getBody();
            BodyDescriptor bodyDescriptor = descriptorsSystem.getBody(body.index);
            bodyAnimation = animationsSystem.getBodyAnimation(body, heading.current);

            headOffsetY = bodyDescriptor.getHeadOffsetY() - getMovementOffsetY();
            headOffsetY *= SCALE;

            bodyRegion = player.isMoving() ? bodyAnimation.getGraphic() : bodyAnimation.getGraphic(0);
            idle = !player.isMoving() ? bodyAnimation.getIdleTime() : 0;
            idle *= SCALE;
            bodyPixelOffsetX = bodyPixelOffsetX + ((Tile.TILE_PIXEL_WIDTH - bodyRegion.getRegionWidth()) / 2);
            bodyPixelOffsetY = screenPos.y - (bodyRegion.getRegionHeight() - Tile.TILE_PIXEL_HEIGHT) - Tile.TILE_PIXEL_HEIGHT;
        }

        /**
         * Renderizado de cuerpos
         */
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

        /**
         * Renderizado de cabezas
         */
        void drawHead() {
            if (player.hasHead()) {
                final Head head = player.getHead();
                AOTexture headTexture = animationsSystem.getHeadAnimation(head, heading.current);
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

        /**
         * Renderizado de Cascos
         */
        void drawHelmet() {
            if (player.hasHelmet()) {
                Helmet helmet = player.getHelmet();
                BundledAnimation animation = animationsSystem.getHelmetsAnimation(helmet, heading.current);
                float offsetY = headOffsetY - 4 * SCALE;
                float offsetX = 4.0f * SCALE;
                draw(animation, this.bodyPixelOffsetX, this.bodyPixelOffsetY, offsetX, offsetY);
            }
        }

        /**
         * Renderizado de Armas
         */
        void drawWeapon() {
            if (player.hasWeapon()) {
                Weapon weapon = player.getWeapon();
                BundledAnimation animation = animationsSystem.getWeaponAnimation(weapon, heading.current);
                draw(animation, this.bodyPixelOffsetX, this.bodyPixelOffsetY, 0, Math.max(0, headOffsetY) + idle);
            }
        }


        /**
         * Renderizado de Escudos
         */
        void drawShield() {
            if (player.hasShield()) {
                Shield shield = player.getShield();
                BundledAnimation animation = animationsSystem.getShieldAnimation(shield, heading.current);
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

        /**
         * Renderiza un grafico (de cuerpo) en pantalla
         * @param region
         * @param x
         * @param y
         * @param offsetX
         * @param offsetY
         */
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
