package game.handlers;

import com.artemis.annotations.Wire;
import com.esotericsoftware.minlog.Log;
import entity.character.equipment.Helmet;
import entity.character.equipment.Shield;
import entity.character.equipment.Weapon;
import entity.character.parts.Body;
import entity.character.parts.Head;
import model.descriptors.*;
import model.textures.BundledAnimation;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import shared.model.Graphic;
import shared.objects.types.HelmetObj;
import shared.objects.types.ShieldObj;
import shared.objects.types.WeaponObj;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Wire
public class AnimationHandler extends PassiveSystem {

    private DescriptorHandler descriptorHandler;
    private ObjectHandler objectHandler;

    // TODO change maps to caches
    private static Map<Body, List<BundledAnimation>> bodyAnimations = new HashMap<>();
    private static Map<Head, List<BundledAnimation>> headAnimations = new HashMap<>();
    private static Map<Helmet, List<BundledAnimation>> helmetAnimations = new HashMap<>();
    private static Map<Weapon, List<BundledAnimation>> weaponAnimations = new HashMap<>();
    private static Map<Shield, List<BundledAnimation>> shieldAnimations = new HashMap<>();

    private static Map<Integer, BundledAnimation> animations = new ConcurrentHashMap<>();


    @Deprecated
    private Map<Integer, List<BundledAnimation>> loadDescriptors(List<?> descriptors) {
        Map<Integer, List<BundledAnimation>> result = new HashMap<>();
        int[] idx = {1};
        descriptors.forEach(descriptor -> result.put(idx[0]++, createAnimations((IDescriptor) descriptor, true)));
        return result;
    }

    @Deprecated
    private Map<Integer, List<BundledAnimation>> loadDescriptors(Map<Integer, ?> descriptors) {
        Map<Integer, List<BundledAnimation>> result = new HashMap<>();
        descriptors.forEach((id, descriptor) -> result.put(id, createAnimations((IDescriptor) descriptor, true)));
        return result;
    }

    private List<BundledAnimation> createAnimations(IDescriptor descriptor, boolean pingpong) {
        Log.info("Animation created: " + Arrays.toString(descriptor.getIndexs()));
        List<BundledAnimation> animations = new ArrayList<>();
        int[] indexes = descriptor.getIndexs();
        for (int grhIndex : indexes) {
            if (grhIndex > 0) {
                animations.add(saveBundledAnimation(grhIndex));
            }
        }
        return animations;
    }

    public BundledAnimation getHeadAnimation(Head head, int current) {
        return headAnimations.computeIfAbsent(head, h -> {
            HeadDescriptor descriptor = descriptorHandler.getHead(h.index - 1);
            return createAnimations(descriptor, false);
        }).get(current);
    }

    public BundledAnimation getBodyAnimation(Body body, int current) {
        return bodyAnimations.computeIfAbsent(body, b -> {
            BodyDescriptor descriptor = descriptorHandler.getBody(b.index);
            return createAnimations(descriptor, true);
        }).get(current);
    }

    public BundledAnimation getWeaponAnimation(Weapon weapon, int current) {
        return weaponAnimations.computeIfAbsent(weapon, w -> {
            WeaponObj weaponObj = (WeaponObj) objectHandler.getObject(w.index).get();
            WeaponDescriptor descriptor = descriptorHandler.getWeapon(Math.max(weaponObj.getAnimationId() - 1, 0));
            return createAnimations(descriptor, true);
        }).get(current);
    }

    public BundledAnimation getHelmetsAnimation(Helmet helmet, int current) {
        return helmetAnimations.computeIfAbsent(helmet, h -> {
            HelmetObj helmetObj = (HelmetObj) objectHandler.getObject(h.index).get();
            HelmetDescriptor descriptor = descriptorHandler.getHelmet(Math.max(helmetObj.getAnimationId() - 1, 0));
            return createAnimations(descriptor, true);
        }).get(current);
    }

    public BundledAnimation getShieldAnimation(Shield shield, int current) {
        return shieldAnimations.computeIfAbsent(shield, s -> {
            ShieldObj shieldObj = (ShieldObj) objectHandler.getObject(s.index).get();
            ShieldDescriptor descriptor = descriptorHandler.getShield(Math.max(shieldObj.getAnimationId() - 1, 0));
            return createAnimations(descriptor, true);
        }).get(current);
    }

    public BundledAnimation getGraphicAnimation(int grhIndex) {
        return Optional.ofNullable(animations.get(grhIndex)).orElseGet(() -> saveBundledAnimation(grhIndex));
    }

    private BundledAnimation saveBundledAnimation(int grhIndex) {
        Log.info("BundledAnimation created:" + grhIndex);
        Graphic graphic = descriptorHandler.getGraphic(grhIndex);
        return saveGraphic(grhIndex, graphic);
    }

    public BundledAnimation saveGraphic(int grhIndex, Graphic graphic) {
        BundledAnimation bundledAnimation = new BundledAnimation(graphic, true);
        animations.put(grhIndex, bundledAnimation);
        return bundledAnimation;
    }
}
