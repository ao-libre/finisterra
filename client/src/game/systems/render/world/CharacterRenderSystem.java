package game.systems.render.world;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import component.entity.character.parts.Body;
import component.entity.character.render.CharRenderInfo;
import component.entity.character.states.Heading;
import component.position.WorldPos;
import game.systems.render.BatchSystem;
import game.systems.render.chars.PrerenderCharCache;
import game.utils.Pos2D;
import game.utils.Resources;
import model.textures.BundledAnimation;

import java.util.Comparator;
import java.util.Optional;

import static com.artemis.E.E;
import static component.entity.character.render.CharRenderInfo.NONE;
import static game.systems.render.world.CharacterRenderingSystem.CharacterDrawer.createDrawer;

/**
 * Clase para el renderizado de personajes
 */
@Wire(injectInherited = true)
public class CharacterRenderSystem extends RenderingSystem {

    private static final Aspect.Builder CHAR_ASPECT = Aspect.all(WorldPos.class, Body.class, Heading.class);
    private PrerenderCharCache cache;
    private BatchSystem batchSystem;
    private static TextureRegion shadow;
    public ShaderProgram shaderOutline;

    public CharacterRenderSystem() {
        super(CHAR_ASPECT);
    }

    private static float getMovementOffset(BundledAnimation bodyAnimation) {
        float animationTime = bodyAnimation.getAnimationTime();
        float interpolationTime = bodyAnimation.getAnimation().getAnimationDuration() / 2;
        return Interpolation.circle.apply(Math.min(1f, animationTime < interpolationTime ? animationTime / interpolationTime : interpolationTime / animationTime));
    }

    @Override
    protected void initialize() {
        shadow = new TextureRegion(new Texture(Resources.GAME_IMAGES_PATH + "shadow.png"));
        loadShader();
    }

    public void loadShader() {
        String vertexShader;
        String fragmentShader;
        vertexShader = Gdx.files.internal(Resources.GAME_SHADERS_PATH + "outlineVertexShader.glsl").readString();
        fragmentShader = Gdx.files.internal(Resources.GAME_SHADERS_PATH + "outlineFragShader.glsl").readString();
        shaderOutline = new ShaderProgram(vertexShader, fragmentShader);
        if (!shaderOutline.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + shaderOutline.getLog());
    }
    @Override
    protected void process(E player) {
        CharRenderInfo charRenderInfo = player.charRenderInfo().getCharRenderInfo();
        charRenderInfo.setHead(player.hasHead() ? player.headIndex() : NONE);
        charRenderInfo.setWeapon(player.hasWeapon() ? player.weaponIndex() : NONE);
        charRenderInfo.setBody(player.hasBody() ? player.bodyIndex() : NONE);
        charRenderInfo.setShield(player.hasShield() ? player.shieldIndex() : NONE);
        charRenderInfo.setHelmet(player.hasHelmet() ? player.helmetIndex() : NONE);
        charRenderInfo.setHeading(player.headingCurrent());
    }

    public Aspect.Builder getAspect() {
        return CHAR_ASPECT;
    }

    public void drawPlayer(E player, Optional<WorldPos> forcedPos) {
        WorldPos pos = forcedPos.orElse(player.getWorldPos());
        Pos2D currentPos = Pos2D.get(pos, player.getWorldPosOffsets());
        Pos2D screenPos = currentPos.toScreen();
        int frame = player.hasCharAnimation() ? getFrame(player) : 0;
        TextureRegion textureRegion = cache.get(player.getCharRenderInfo(), frame);
        // outline shader
        batchSystem.getBatch().draw(textureRegion, screenPos.x, screenPos.y - textureRegion.getRegionHeight());
    }

    private int getFrame(E player) {
        int frameCount;
        switch (player.headingCurrent()) {
            case Heading.HEADING_SOUTH | Heading.HEADING_NORTH:
                frameCount = 6;
                break;
            default:
                frameCount = 5;
                break;
        }
        float duration = player.charAnimationDuration();
        float frameDuration = duration / frameCount;
        float animationTime = player.charAnimationTime();
        return MathUtils.clamp((int) (animationTime / frameDuration), 0, frameCount - 1);
    }

    @Override
    protected Comparator<? super Entity> getComparator() {
        return (entity1, entity2) -> E(entity2).getWorldPos().y - E(entity1).getWorldPos().y;
    }


}
