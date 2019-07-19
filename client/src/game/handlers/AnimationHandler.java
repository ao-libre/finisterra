package game.handlers;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.esotericsoftware.minlog.Log;
import entity.character.equipment.Helmet;
import entity.character.equipment.Shield;
import entity.character.equipment.Weapon;
import entity.character.parts.Body;
import entity.character.parts.Head;
import game.AssetManagerHolder;
import model.descriptors.*;
import model.textures.AOAnimation;
import model.textures.AOImage;
import model.textures.AOTexture;
import model.textures.BundledAnimation;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import shared.objects.types.HelmetObj;
import shared.objects.types.ShieldObj;
import shared.objects.types.WeaponObj;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Wire
public class AnimationHandler extends PassiveSystem {

    // TODO change maps to caches
    private static Map<Body, List<BundledAnimation>> bodyAnimations = new HashMap<>();
    private static Map<Head, List<AOTexture>> headAnimations = new HashMap<>();
    private static Map<Helmet, List<BundledAnimation>> helmetAnimations = new HashMap<>();
    private static Map<Weapon, List<BundledAnimation>> weaponAnimations = new HashMap<>();
    private static Map<Shield, List<BundledAnimation>> shieldAnimations = new HashMap<>();
    private static Map<Integer, BundledAnimation> bundledAnimations = new ConcurrentHashMap<>();
    private static Map<Integer, AOTexture> textures = new ConcurrentHashMap<>();

    private AOAssetManager assetManager;
    private DescriptorHandler descriptorHandler;
    private ObjectHandler objectHandler;

    public AnimationHandler() {
        AssetManagerHolder game = (AssetManagerHolder) Gdx.app.getApplicationListener();
        assetManager = game.getAssetManager();
    }

    private List<BundledAnimation> createAnimations(IDescriptor descriptor) {
        Log.info("Animation created: " + Arrays.toString(descriptor.getIndexs()));
        List<BundledAnimation> animations = new ArrayList<>();
        int[] indexes = descriptor.getIndexs();
        for (int grhIndex : indexes) {
            if (grhIndex > 0) {
                animations.add(saveAnimation(grhIndex));
            }
        }
        return animations;
    }

    private List<AOTexture> createTextures(HeadDescriptor descriptor) {
        List<AOTexture> heads = new ArrayList<>();
        int[] indexes = descriptor.getIndexs();
        for (int id : indexes) {
            AOTexture aoTexture = saveTexture(id);
            heads.add(aoTexture);
        }
        return heads;
    }

    public AOTexture getHeadAnimation(Head head, int current) {
        return headAnimations.computeIfAbsent(head, h -> {
            HeadDescriptor descriptor = descriptorHandler.getHead(h.index - 1);
            return createTextures(descriptor);
        }).get(current);
    }

    public BundledAnimation getBodyAnimation(Body body, int current) {
        return bodyAnimations.computeIfAbsent(body, b -> {
            BodyDescriptor descriptor = descriptorHandler.getBody(b.index);
            return createAnimations(descriptor);
        }).get(current);
    }

    public BundledAnimation getWeaponAnimation(Weapon weapon, int current) {
        return weaponAnimations.computeIfAbsent(weapon, w -> {
            WeaponObj weaponObj = (WeaponObj) objectHandler.getObject(w.index).get();
            WeaponDescriptor descriptor = descriptorHandler.getWeapon(Math.max(weaponObj.getAnimationId() - 1, 0));
            return createAnimations(descriptor);
        }).get(current);
    }

    public BundledAnimation getHelmetsAnimation(Helmet helmet, int current) {
        return helmetAnimations.computeIfAbsent(helmet, h -> {
            HelmetObj helmetObj = (HelmetObj) objectHandler.getObject(h.index).get();
            HelmetDescriptor descriptor = descriptorHandler.getHelmet(Math.max(helmetObj.getAnimationId() - 1, 0));
            return createAnimations(descriptor);
        }).get(current);
    }

    public BundledAnimation getShieldAnimation(Shield shield, int current) {
        return shieldAnimations.computeIfAbsent(shield, s -> {
            ShieldObj shieldObj = (ShieldObj) objectHandler.getObject(s.index).get();
            ShieldDescriptor descriptor = descriptorHandler.getShield(Math.max(shieldObj.getAnimationId() - 1, 0));
            return createAnimations(descriptor);
        }).get(current);
    }

    public AOTexture getTexture(int id) {
        return Optional.ofNullable(textures.get(id)).orElseGet(() -> saveTexture(id));
    }

    public BundledAnimation getAnimation(int id) {
        return Optional.ofNullable(bundledAnimations.get(id)).orElseGet(() -> saveAnimation(id));
    }

    private BundledAnimation saveAnimation(int id) {
        AOAnimation animation = assetManager.getAnimation(id);
        if (animation == null) {
            Log.info("Fail to create animation for: " + id);
            return bundledAnimations.get(0);
        }
        return saveAnimation(animation);
    }

    private BundledAnimation saveAnimation(AOAnimation animation) {
        BundledAnimation bundledAnimation = new BundledAnimation(animation);
        bundledAnimations.put(animation.getId(), bundledAnimation);
        return bundledAnimation;
    }

    private AOTexture saveTexture(int id) {
        AOImage image = assetManager.getImage(id);
        if (image == null) {
            Log.info("Fail to create AO Image: " + id);
            return textures.get(0);
        }
        return saveTexture(image);
    }

    private AOTexture saveTexture(AOImage image) {
        AOTexture aoTexture = new AOTexture(image);
        textures.put(image.getId(), aoTexture);
        return aoTexture;
    }
}
