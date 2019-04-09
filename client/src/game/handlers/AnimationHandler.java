package game.handlers;

import entity.character.equipment.Helmet;
import entity.character.equipment.Shield;
import entity.character.equipment.Weapon;
import entity.character.parts.Body;
import entity.character.parts.Head;
import graphics.FX;
import model.descriptors.*;
import model.textures.BundledAnimation;
import shared.model.Graphic;
import shared.objects.types.HelmetObj;
import shared.objects.types.ShieldObj;
import shared.objects.types.WeaponObj;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AnimationHandler {

    // TODO check
    private static Map<Body, List<BundledAnimation>> bodyAnimations = new WeakHashMap<>();
    private static Map<Head, List<BundledAnimation>> headAnimations = new WeakHashMap<>();
    private static Map<Helmet, List<BundledAnimation>> helmetAnimations = new WeakHashMap<>();
    private static Map<Weapon, List<BundledAnimation>> weaponAnimations = new WeakHashMap<>();
    private static Map<Shield, List<BundledAnimation>> shieldAnimations = new WeakHashMap<>();
    private static Map<FX, List<BundledAnimation>> fxAnimations = new WeakHashMap<>();

    private static Map<Integer, BundledAnimation> animations = new ConcurrentHashMap<>();

    public static void load() {
//        bodyAnimations = loadDescriptors(DescriptorHandler.getBodies());
//        headAnimations = loadDescriptors(DescriptorHandler.getHeads());
//        helmetAnimations = loadDescriptors(DescriptorHandler.getHelmets());
//        weaponAnimations = loadDescriptors(DescriptorHandler.getWeapons());
//        shieldAnimations = loadDescriptors(DescriptorHandler.getShields());
//        fxAnimations = loadDescriptors(DescriptorHandler.getFxs());
    }

    private static Map<Integer, List<BundledAnimation>> loadDescriptors(List<?> descriptors) {
        Map<Integer, List<BundledAnimation>> result = new HashMap<>();
        int[] idx = {1};
        descriptors.forEach(descriptor -> {
            result.put(idx[0]++, createAnimations((IDescriptor) descriptor, true));
        });
        return result;
    }

    private static Map<Integer, List<BundledAnimation>> loadDescriptors(Map<Integer, ?> descriptors) {
        Map<Integer, List<BundledAnimation>> result = new HashMap<>();
        descriptors.forEach((id, descriptor) -> {
            result.put(id, createAnimations((IDescriptor) descriptor, true));
        });
        return result;
    }

    private static List<BundledAnimation> createAnimations(IDescriptor descriptor, boolean pingpong) {
        List<BundledAnimation> animations = new ArrayList<>();
        int[] indexs = descriptor.getIndexs();
        for (int i = 0; i < indexs.length; i++) {
            Graphic grh = DescriptorHandler.getGraphic(indexs[i]);
            if (grh != null) {
                animations.add(new BundledAnimation(grh, pingpong));
            }
        }
        return animations;
    }

    public static BundledAnimation getHeadAnimation(Head head, int current) {
        return headAnimations.computeIfAbsent(head, h -> {
            HeadDescriptor descriptor = DescriptorHandler.getHead(h.index - 1);
            return createAnimations(descriptor, false);
        }).get(current);
    }

    public static BundledAnimation getBodyAnimation(Body body, int current) {
        return bodyAnimations.computeIfAbsent(body, b -> {
            BodyDescriptor descriptor = DescriptorHandler.getBody(b.index);
            return createAnimations(descriptor, true);
        }).get(current);
    }

    public static BundledAnimation getWeaponAnimation(Weapon weapon, int current) {
        return weaponAnimations.computeIfAbsent(weapon, w -> {
            WeaponObj weaponObj = (WeaponObj) ObjectHandler.getObject(w.index).get();
            WeaponDescriptor descriptor = DescriptorHandler.getWeapon(Math.max(weaponObj.getAnimationId() - 1, 0));
            return createAnimations(descriptor, true);
        }).get(current);
    }

    public static BundledAnimation getHelmetsAnimation(Helmet helmet, int current) {
        return helmetAnimations.computeIfAbsent(helmet, h -> {
            HelmetObj helmetObj = (HelmetObj) ObjectHandler.getObject(h.index).get();
            HelmetDescriptor descriptor = DescriptorHandler.getHelmet(Math.max(helmetObj.getAnimationId() - 1, 0));
            return createAnimations(descriptor, true);
        }).get(current);
    }

    public static BundledAnimation getShieldAnimation(Shield shield, int current) {
        return shieldAnimations.computeIfAbsent(shield, s -> {
            ShieldObj shieldObj = (ShieldObj) ObjectHandler.getObject(s.index).get();
            ShieldDescriptor descriptor = DescriptorHandler.getShield(Math.max(shieldObj.getAnimationId() - 1, 0));
            return createAnimations(descriptor, true);
        }).get(current);
    }

    public static BundledAnimation getFXAnimation(int index, int current) {
        return fxAnimations.get(index).get(current);
    }

    public static Map<Body, List<BundledAnimation>> getBodyAnimations() {
        return bodyAnimations;
    }

    public static Map<FX, List<BundledAnimation>> getFxAnimations() {
        return fxAnimations;
    }

    public static Map<Head, List<BundledAnimation>> getHeadAnimations() {
        return headAnimations;
    }

    public static Map<Helmet, List<BundledAnimation>> getHelmetAnimations() {
        return helmetAnimations;
    }

    public static Map<Shield, List<BundledAnimation>> getShieldAnimations() {
        return shieldAnimations;
    }

    public static Map<Weapon, List<BundledAnimation>> getWeaponAnimations() {
        return weaponAnimations;
    }

    public static BundledAnimation getGraphicAnimation(int grhIndex) {
        return Optional.ofNullable(animations.get(grhIndex)).orElseGet(() -> saveBundledAnimation(grhIndex));
    }

    public static BundledAnimation saveBundledAnimation(int grhIndex) {
        BundledAnimation bundledAnimation = new BundledAnimation(DescriptorHandler.getGraphic(grhIndex), false);
        animations.put(grhIndex, bundledAnimation);
        return bundledAnimation;
    }
}