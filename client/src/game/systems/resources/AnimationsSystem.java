package game.systems.resources;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.esotericsoftware.minlog.Log;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import component.entity.Index;
import component.entity.character.equipment.Helmet;
import component.entity.character.equipment.Shield;
import component.entity.character.equipment.Weapon;
import component.entity.character.parts.Body;
import component.entity.character.parts.Head;
import component.graphic.Effect;
import game.handlers.DefaultAOAssetManager;
import model.descriptors.HeadDescriptor;
import model.descriptors.IDescriptor;
import model.textures.AOAnimation;
import model.textures.AOImage;
import model.textures.AOTexture;
import model.textures.BundledAnimation;
import org.jetbrains.annotations.NotNull;
import shared.objects.types.HelmetObj;
import shared.objects.types.ShieldObj;
import shared.objects.types.WeaponObj;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Wire
public class AnimationsSystem extends BaseSystem {

    private static LoadingCache<Integer, AOTexture> textures;
    private static LoadingCache<Index, List<AOTexture>> headAnimations;
    private static LoadingCache<Index, List<BundledAnimation>> helmetAnimations;
    private static LoadingCache<Index, List<BundledAnimation>> weaponAnimations;
    private static LoadingCache<Index, List<BundledAnimation>> shieldAnimations;
    private static LoadingCache<Index, List<BundledAnimation>> bodyAnimations;
    private static LoadingCache<Index, List<BundledAnimation>> fxAnimations;
    private static LoadingCache<Integer, BundledAnimation> tiledAnimations;
    // Injected Systems
    private DescriptorsSystem descriptorsSystem;
    private ObjectSystem objectSystem;
    @Wire
    private DefaultAOAssetManager assetManager;
    private LoadingCache<AOAnimation, BundledAnimation> previews = CacheBuilder
            .newBuilder()
            .expireAfterAccess(3, TimeUnit.MINUTES)
            .build(CacheLoader.from(this::createAnimation));

    public AnimationsSystem() {
        tiledAnimations = CacheBuilder
                .newBuilder()
                .expireAfterAccess(1, TimeUnit.MINUTES)
                .build(CacheLoader.from(key -> {
                    AOAnimation animation = assetManager.getAnimation(key);
                    return createAnimation(animation);
                }));

        headAnimations = CacheBuilder.newBuilder().expireAfterAccess(3, TimeUnit.MINUTES)
                .build(CacheLoader.from(head -> {
                    HeadDescriptor descriptor = descriptorsSystem.getHead(head.getIndex());
                    return createTextures(descriptor);
                }));
        bodyAnimations = createCache((body) -> descriptorsSystem.getBody(body.getIndex()));
        helmetAnimations = createCache((helmet) -> {
            HelmetObj helmetObj = (HelmetObj) objectSystem.getObject(helmet.getIndex()).get();
            return descriptorsSystem.getHelmet(Math.max(helmetObj.getAnimationId(), 0));
        });
        weaponAnimations = createCache((weapon) -> {
            WeaponObj weaponObj = (WeaponObj) objectSystem.getObject(weapon.getIndex()).get();
            return descriptorsSystem.getWeapon(Math.max(weaponObj.getAnimationId(), 0));
        });
        shieldAnimations = createCache((shield) -> {
            ShieldObj shieldObj = (ShieldObj) objectSystem.getObject(shield.getIndex()).get();
            return descriptorsSystem.getShield(Math.max(shieldObj.getAnimationId(), 0));
        });
        fxAnimations = createCache((fx) -> descriptorsSystem.getFX(fx.getIndex()));

        textures = CacheBuilder.newBuilder()
                .expireAfterAccess(3, TimeUnit.MINUTES)
                .build(CacheLoader.from(this::createTexture));
    }

    private LoadingCache<Index, List<BundledAnimation>> createCache(Function<Index, IDescriptor> fun) {
        return CacheBuilder
                .newBuilder()
                .expireAfterAccess(3, TimeUnit.MINUTES)
                .build(CacheLoader.from(object -> {
                    assert object != null;
                    IDescriptor descriptor = fun.apply(object);
                    return createAnimations(descriptor);
                }));
    }

    private List<BundledAnimation> createAnimations(IDescriptor descriptor) {
        Log.debug("Animation created: " + Arrays.toString(descriptor.getIndexs()));
        List<BundledAnimation> animations = new ArrayList<>();
        int[] indexes = descriptor.getIndexs();
        for (int grhIndex : indexes) {
            if (grhIndex > 0) {
                animations.add(createAnimation(grhIndex));
            }
        }
        return animations;
    }

    private List<AOTexture> createTextures(HeadDescriptor descriptor) {
        List<AOTexture> heads = new ArrayList<>();
        int[] indexes = descriptor.getIndexs();
        for (int id : indexes) {
            AOTexture aoTexture = textures.getUnchecked(id);
            heads.add(aoTexture);
        }
        return heads;
    }

    public AOTexture getHeadAnimation(Head head, int current) {
        return headAnimations.getUnchecked(head).get(current);
    }

    public BundledAnimation getBodyAnimation(Body body, int current) {
        return bodyAnimations.getUnchecked(body).get(current);
    }

    public BundledAnimation getWeaponAnimation(Weapon weapon, int current) {
        return weaponAnimations.getUnchecked(weapon).get(current);
    }

    public BundledAnimation getHelmetsAnimation(Helmet helmet, int current) {
        return helmetAnimations.getUnchecked(helmet).get(current);
    }

    public BundledAnimation getShieldAnimation(Shield shield, int current) {
        return shieldAnimations.getUnchecked(shield).get(current);
    }

    public AOTexture getTexture(int id) {
        return textures.getUnchecked(id);
    }

    public BundledAnimation getFX(Effect e) {
        return fxAnimations.getUnchecked(e).get(0);
    }

    public BundledAnimation getTiledAnimation(int id) {
        return tiledAnimations.getUnchecked(id);
    }

    public BundledAnimation getPreviewAnimation(AOAnimation animation) {
        return previews.getUnchecked(animation);
    }

    public BundledAnimation createAnimation(int id) {
        AOAnimation animation = assetManager.getAnimation(id);
        if (animation == null) {
            Log.debug("Fail to create animation for: " + id);
            return null;
        }
        return createAnimation(animation);
    }

    @NotNull
    public BundledAnimation createAnimation(AOAnimation animation) {
        return new BundledAnimation(getAnimationTextures(animation), animation.getSpeed());
    }

    private TextureRegion[] getAnimationTextures(AOAnimation aoAnimation) {
        return Arrays.stream(aoAnimation.getFrames())
                .filter(i -> i > 0)
                .mapToObj(this::getTexture)
                .map(AOTexture::getTexture)
                .toArray(TextureRegion[]::new);
    }


    private AOTexture createTexture(int id) {
        AOImage image = assetManager.getImage(id);
        if (image == null) {
            Log.debug("Fail to create AO Image: " + id);
            return null;
        }
        return new AOTexture(image, assetManager.getTexture(image.getFileNum()));
    }

    public boolean hasTexture(int id) {
        return assetManager.getImage(id) != null;
    }

    public void clearAnimation(AOAnimation animation) {
        previews.invalidate(animation);
    }

    public void clearImage(AOImage image) {
        textures.invalidate(image.getId());
    }

    @Override
    protected void processSystem() {
        tiledAnimations.asMap().forEach((id, anim) -> {
            anim.setAnimationTime(anim.getAnimationTime() + world.getDelta());
        });
    }
}
