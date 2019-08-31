package game.handlers;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.esotericsoftware.minlog.Log;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import entity.Index;
import entity.character.equipment.Helmet;
import entity.character.equipment.Shield;
import entity.character.equipment.Weapon;
import entity.character.parts.Body;
import entity.character.parts.Head;
import graphics.Effect;
import model.descriptors.*;
import model.textures.AOAnimation;
import model.textures.AOImage;
import model.textures.AOTexture;
import model.textures.BundledAnimation;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import shared.objects.types.HelmetObj;
import shared.objects.types.ShieldObj;
import shared.objects.types.WeaponObj;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Wire
public class AnimationHandler extends BaseSystem {

    // Injected Systems
    private DescriptorHandler descriptorHandler;
    private ObjectHandler objectHandler;

    private AOAssetManager assetManager;

    private static LoadingCache<Integer, AOTexture> textures;

    private static LoadingCache<Index, List<AOTexture>> headAnimations;

    private static LoadingCache<Index, List<BundledAnimation>> helmetAnimations;
    private static LoadingCache<Index, List<BundledAnimation>> weaponAnimations;
    private static LoadingCache<Index, List<BundledAnimation>> shieldAnimations;
    private static LoadingCache<Index, List<BundledAnimation>> bodyAnimations;
    private static LoadingCache<Effect, BundledAnimation> fxAnimations;

    private static LoadingCache<Integer, BundledAnimation> tiledAnimations;

    private LoadingCache<AOAnimation, BundledAnimation> previews = CacheBuilder
            .newBuilder()
            .expireAfterAccess(3, TimeUnit.MINUTES)
            .build(CacheLoader.from(BundledAnimation::new));

    public AnimationHandler(AOAssetManager assetManager) {
        this.assetManager = assetManager;

        tiledAnimations = CacheBuilder
                .newBuilder()
                .expireAfterAccess(1, TimeUnit.MINUTES)
                .build(CacheLoader.from(key -> {
                    AOAnimation animation = assetManager.getAnimation(key);
                    return new BundledAnimation(animation);
                }));

        headAnimations = CacheBuilder.newBuilder().expireAfterAccess(3, TimeUnit.MINUTES)
                .build(CacheLoader.from(head -> {
                    HeadDescriptor descriptor = descriptorHandler.getHead(head.getIndex());
                    return createTextures(descriptor);
                }));
        bodyAnimations = createCache((body) -> descriptorHandler.getBody(body.getIndex()));
        helmetAnimations = createCache((helmet) -> {
            HelmetObj helmetObj = (HelmetObj) objectHandler.getObject(helmet.getIndex()).get();
            return descriptorHandler.getHelmet(Math.max(helmetObj.getAnimationId(), 0));
        });
        weaponAnimations = createCache((weapon) -> {
            WeaponObj weaponObj = (WeaponObj) objectHandler.getObject(weapon.getIndex()).get();
            return descriptorHandler.getWeapon(Math.max(weaponObj.getAnimationId(), 0));
        });
        shieldAnimations = createCache((shield) -> {
            ShieldObj shieldObj = (ShieldObj) objectHandler.getObject(shield.getIndex()).get();
            return descriptorHandler.getShield(Math.max(shieldObj.getAnimationId(), 0));
        });
        fxAnimations = createFXCache();

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

    private LoadingCache<Effect, BundledAnimation> createFXCache() {
        return CacheBuilder
                .newBuilder()
                .expireAfterAccess(3, TimeUnit.MINUTES)
                .build(CacheLoader.from(effect -> {
                    assert effect != null;
                    return createFX(effect);
                }));
    }

    private List<BundledAnimation> createAnimations(IDescriptor descriptor) {
        Log.info("Animation created: " + Arrays.toString(descriptor.getIndexs()));
        List<BundledAnimation> animations = new ArrayList<>();
        int[] indexes = descriptor.getIndexs();
        for (int grhIndex : indexes) {
            if (grhIndex > 0) {
                animations.add(createAnimation(grhIndex));
            }
        }
        return animations;
    }

    private BundledAnimation createFX(Effect effect) {
        FXDescriptor descriptor = descriptorHandler.getFX(effect.getIndex());
        int grhIndex = descriptor.getIndexs()[0];
        BundledAnimation animation = createAnimation(grhIndex);
        animation.setLoops(effect.loops);
        return animation;
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
        return fxAnimations.getUnchecked(e);
    }

    public BundledAnimation getTiledAnimation(int id) {
        return tiledAnimations.getUnchecked(id);
    }

    public BundledAnimation getPreviewAnimation(AOAnimation animation) {
        return previews.getUnchecked(animation);
    }

    private BundledAnimation createAnimation(int id) {
        AOAnimation animation = assetManager.getAnimation(id);
        if (animation == null) {
            Log.info("Fail to create animation for: " + id);
            return null;
        }
        return new BundledAnimation(animation);
    }

    private AOTexture createTexture(int id) {
        AOImage image = assetManager.getImage(id);
        if (image == null) {
            Log.info("Fail to create AO Image: " + id);
            return null;
        }
        return new AOTexture(image);
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
