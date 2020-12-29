package game.systems.render.chars;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.esotericsoftware.minlog.Log;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import component.entity.character.render.CharRenderInfo;
import component.entity.character.states.Heading;
import game.handlers.DefaultAOAssetManager;
import game.systems.resources.DescriptorsSystem;
import game.systems.resources.ObjectSystem;
import game.utils.Colors;
import game.utils.Pixmaps;
import model.descriptors.*;
import model.textures.AOAnimation;
import model.textures.AOImage;
import model.textures.AOTexture;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import shared.model.map.Tile;
import shared.objects.types.HelmetObj;
import shared.objects.types.ShieldObj;
import shared.objects.types.WeaponObj;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;

import static component.entity.character.render.CharRenderInfo.NONE;

@Wire
public class PrerenderCharCache extends PassiveSystem {

    @Wire
    private DefaultAOAssetManager assetManager;
    private ObjectSystem objectSystem;
    private DescriptorsSystem descriptorsSystem;

    LoadingCache<CharRenderInfo, CharFrames> prerenderChars =
            CacheBuilder.newBuilder().expireAfterAccess(Duration.ofMinutes(5)).removalListener((notification) -> ((CharFrames) notification.getValue()).dispose()).build(CacheLoader.from(info -> new CharFrames(info, this::drawPixmap)));


    public TextureRegion get(CharRenderInfo info, int frame) {
        try {
            return prerenderChars.get(info).getFrame(frame);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Pixmap drawPixmap(CharRenderInfo info, int i) {
        int heading = info.getHeading();
        int headOffsetY = info.getHead() == NONE ? 0 : descriptorsSystem.getBody(info.getBody()).headOffsetY;
        int headOffsetX = 0;
        int extraPixel;
        if (heading == Heading.HEADING_SOUTH || heading == Heading.HEADING_NORTH) {
            extraPixel = i == 2 || i == 4 ? 1 : i == 1 || i == 3 ? -1 : 0;
            if (heading == Heading.HEADING_SOUTH) {
                extraPixel *= -1;
            }
            headOffsetY += extraPixel;
        } else {
            extraPixel = i == 1 || i == 4 || i == 2 ? 1 : 0;
            if (heading == Heading.HEADING_EAST) {
                headOffsetX = 1;
                headOffsetX += extraPixel;
            } else {
                headOffsetX = -1;
                headOffsetX -= extraPixel;
            }
        }

        Pixmap frame = new Pixmap((int) Tile.TILE_PIXEL_WIDTH, 64, Pixmap.Format.RGBA8888);
        switch (heading) {
            case Heading.HEADING_NORTH -> {
                drawWeapon(frame, info.getWeapon(), heading, i);
                drawShield(frame, info.getShield(), heading, i);
                drawBody(frame, info.getBody(), heading, i, extraPixel);
                drawHead(frame, info.getHead(), heading, i, headOffsetY, headOffsetX);
                drawHelmet(frame, info.getHelmet(), heading, i, headOffsetY);
            }
            case Heading.HEADING_SOUTH -> {
                drawBody(frame, info.getBody(), heading, i, extraPixel);
                drawHead(frame, info.getHead(), heading, i, headOffsetY, headOffsetX);
                drawHelmet(frame, info.getHelmet(), heading, i, headOffsetY);
                drawWeapon(frame, info.getWeapon(), heading, i);
                drawShield(frame, info.getShield(), heading, i);
            }
            case Heading.HEADING_EAST -> {
                drawShield(frame, info.getShield(), heading, i);
                drawBody(frame, info.getBody(), heading, i, extraPixel);
                drawHead(frame, info.getHead(), heading, i, headOffsetY, headOffsetX);
                drawHelmet(frame, info.getHelmet(), heading, i, headOffsetY);
                drawWeapon(frame, info.getWeapon(), heading, i);
            }
            case Heading.HEADING_WEST -> {
                drawWeapon(frame, info.getWeapon(), heading, i);
                drawBody(frame, info.getBody(), heading, i, extraPixel);
                drawHead(frame, info.getHead(), heading, i, headOffsetY, headOffsetX);
                drawHelmet(frame, info.getHelmet(), heading, i, headOffsetY);
                drawShield(frame, info.getShield(), heading, i);
            }
        }
        return frame;
    }

    private void drawWeapon(Pixmap frame, int id, int heading, int i) {
        if (id == NONE) return;
        objectSystem.getObject(id).map(WeaponObj.class::cast).ifPresent(weaponObj -> {
            WeaponDescriptor weapon = descriptorsSystem.getWeapon(weaponObj.getAnimationId());
            int animationId = weapon.getIndexs()[heading];
            AOAnimation animation = assetManager.getAnimation(animationId);

            if (animation.getFrames().length <= i) return;
            int frameId = animation.getFrames()[i];
            AOTexture texture = createTexture(frameId);

            drawTextureInFrame(frame, texture.getTexture(), 0, 0);
        });
    }

    private void drawBody(Pixmap frame, int id, int heading, int i, int extraPixel) {
        if (id == NONE) return;
        BodyDescriptor body = descriptorsSystem.getBody(id);
        int bodyIndex = body.getIndexs()[heading];
        AOAnimation animation = assetManager.getAnimation(bodyIndex);

        if (animation.getFrames().length <= i) return;
        int frameId = animation.getFrames()[i];
        AOTexture texture = createTexture(frameId);
        drawTextureInFrame(frame, texture.getTexture(), 0, Math.max(extraPixel, 0));
    }

    private void drawHead(Pixmap frame, int id, int heading, int i, int headOffset, int extraPixel) {
        if (id == NONE) return;
        HeadDescriptor descriptor = descriptorsSystem.getHead(id);
        int headId = descriptor.getIndexs()[heading];
        AOTexture texture = createTexture(headId);

        drawTextureInFrame(frame, texture.getTexture(), extraPixel, headOffset);
    }

    private void drawHelmet(Pixmap frame, int id, int heading, int i, int headOffset) {
        if (id == NONE) return;
        objectSystem.getObject(id).map(HelmetObj.class::cast).ifPresent(helmetObj -> {
            HelmetDescriptor helmet = descriptorsSystem.getHelmet(helmetObj.getAnimationId());
            int helmetId = helmet.getIndexs()[heading];
            AOTexture texture = createTexture(helmetId);

            drawTextureInFrame(frame, texture.getTexture(), 0, 45);
        });
    }

    private void drawShield(Pixmap frame, int id, int heading, int i) {
        if (id == NONE) return;
        objectSystem.getObject(id).map(ShieldObj.class::cast).ifPresent(shieldObj -> {
            ShieldDescriptor shield = descriptorsSystem.getShield(shieldObj.getAnimationId());
            int animationId = shield.getIndexs()[heading];
            AOAnimation animation = assetManager.getAnimation(animationId);

            if (animation.getFrames().length <= i) return;
            int frameId = animation.getFrames()[i];
            AOTexture texture = createTexture(frameId);

            drawTextureInFrame(frame, texture.getTexture(), 0, 0);
        });
    }

    private void drawTextureInFrame(Pixmap frame, TextureRegion textureRegion, int x, int y) {
        if (textureRegion == null) return;
        TextureData textureData = textureRegion.getTexture().getTextureData();
        if (!textureData.isPrepared()) {
            textureData.prepare();
        }

        frame.drawPixmap(
                textureData.consumePixmap(), // The other Pixmap
                (frame.getWidth() - textureRegion.getRegionWidth()) / 2 + x, // The target x-coordinate (top left corner)
                frame.getHeight() - textureRegion.getRegionHeight() + y, // The target y-coordinate (top left corner)
                textureRegion.getRegionX(), // The source x-coordinate (top left corner)
                textureRegion.getRegionY(), // The source y-coordinate (top left corner)
                textureRegion.getRegionWidth(), // The width of the area from the other Pixmap in pixels
                textureRegion.getRegionHeight() // The height of the area from the other Pixmap in pixels
        );
    }

    private AOTexture createTexture(int id) {
        AOImage image = assetManager.getImage(id);
        if (image == null) {
            Log.debug("Fail to create AO Image: " + id);
            return null;
        }
        return new AOTexture(image, assetManager.getTexture(image.getFileNum()), false);
    }

    public static class CharFrames {
        private final TextureAtlas incrementalTextureAtlas;
        private final PixmapPacker pixmapPacker;
        private final CharRenderInfo info;
        private final BiFunction<CharRenderInfo, Integer, Pixmap> drawPixmap;
        private final Map<String, TextureRegion> textureRegionMap;

        public CharFrames(CharRenderInfo info, BiFunction<CharRenderInfo, Integer, Pixmap> drawPixmap) {
            this.info = info;
            this.drawPixmap = drawPixmap;
            pixmapPacker = new PixmapPacker(512, 512, Pixmap.Format.RGBA8888, 1, false);
            incrementalTextureAtlas = pixmapPacker.generateTextureAtlas(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear, false);
            textureRegionMap = new HashMap<>();
        }

        public TextureRegion getFrame(int i) {
            return textureRegionMap.computeIfAbsent(getName(i), s -> addFrame(s, i));
        }

        private TextureRegion addFrame(String s, int i) {
            pixmapPacker.pack(s, drawPixmap.apply(info, i));
            pixmapPacker.updateTextureAtlas(incrementalTextureAtlas, Texture.TextureFilter.Linear, Texture.TextureFilter.Linear, false);
            final TextureAtlas.AtlasRegion region = incrementalTextureAtlas.findRegion(s);
            region.flip(false, true);
            return region;
        }

        private String getName(int i) {
            return String.format("%d-%d-%d-%d-%d-%d-%d", info.getBody(), info.getHead(), info.getHeading(),
                    info.getHelmet(), info.getShield(), info.getWeapon(), i);
        }

        private void dispose() {
            incrementalTextureAtlas.dispose();
        }
    }

}