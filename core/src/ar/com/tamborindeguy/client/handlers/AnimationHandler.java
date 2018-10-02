package ar.com.tamborindeguy.client.handlers;

import ar.com.tamborindeguy.model.Graphic;
import ar.com.tamborindeguy.model.descriptors.IDescriptor;
import ar.com.tamborindeguy.model.textures.BundledAnimation;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AnimationHandler {
    private static Map<Integer, List<BundledAnimation>> bodyAnimations;
    private static Map<Integer, List<BundledAnimation>> headAnimations;
    private static Map<Integer, List<BundledAnimation>> helmetAnimations;
    private static Map<Integer, List<BundledAnimation>> weaponAnimations;
    private static Map<Integer, List<BundledAnimation>> shieldAnimations;
    private static Map<Integer, List<BundledAnimation>> fxAnimations;

    private static Map<Integer, BundledAnimation> animations = new ConcurrentHashMap<>();

    public static void load() {
        bodyAnimations = loadDescriptors(DescriptorHandler.getBodies());
        headAnimations = loadDescriptors(DescriptorHandler.getHeads());
        helmetAnimations = loadDescriptors(DescriptorHandler.getHelmets());
        weaponAnimations = loadDescriptors(DescriptorHandler.getWeapons());
        shieldAnimations = loadDescriptors(DescriptorHandler.getShields());
        fxAnimations = loadDescriptors(DescriptorHandler.getFxs());
    }

    private static Map<Integer, List<BundledAnimation>> loadDescriptors(List<?> descriptors) {
        Map<Integer, List<BundledAnimation>> result = new HashMap<>();
        int[] idx = { 1 };
        descriptors.forEach(descriptor -> {
            result.put(idx[0]++, createAnimations((IDescriptor) descriptor));
        });
        return result;
    }

    private static Map<Integer, List<BundledAnimation>> loadDescriptors(Map<Integer, ?> descriptors) {
        Map<Integer, List<BundledAnimation>> result = new HashMap<>();
        descriptors.forEach((id, descriptor) -> {
            result.put(id, createAnimations((IDescriptor) descriptor));
        });
        return result;
    }

    private static List<BundledAnimation> createAnimations(IDescriptor descriptor) {
        List<BundledAnimation> animations = new ArrayList<>();
        int[] indexs = descriptor.getIndexs();
        for (int i = 0; i < indexs.length; i++) {
            Graphic grh = DescriptorHandler.getGraphic(indexs[i]);
            if (grh != null) {
                animations.add(new BundledAnimation(DescriptorHandler.getGraphic(indexs[i])));
            }
        }
        return animations;
    }

    public static BundledAnimation getHeadAnimation(int index, int current) {
        return headAnimations.get(index).get(current);
    }

    public static BundledAnimation getBodyAnimation(int index, int current) {
        return bodyAnimations.get(index).get(current);
    }

    public static BundledAnimation getWeaponAnimation(int index, int current) {
        return weaponAnimations.get(index).get(current);
    }

    public static BundledAnimation getHelmetsAnimation(int index, int current) {
        return helmetAnimations.get(index).get(current);
    }

    public static BundledAnimation getShieldAnimation(int index, int current) {
        return shieldAnimations.get(index).get(current);
    }

    public static BundledAnimation getFXAnimation(int index, int current) {
        return fxAnimations.get(index).get(current);
    }

    public static BundledAnimation getGraphicAnimation(int grhIndex) {
        return Optional.ofNullable(animations.get(grhIndex)).orElseGet(() -> saveBundledAnimation(grhIndex));
    }

    public static BundledAnimation saveBundledAnimation(int grhIndex) {
        BundledAnimation bundledAnimation = new BundledAnimation(DescriptorHandler.getGraphic(grhIndex));
        animations.put(grhIndex, bundledAnimation);
        return bundledAnimation;
    }
}