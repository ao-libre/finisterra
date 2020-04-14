package shared.util;

import com.google.common.collect.Sets;
import shared.objects.types.*;

import java.util.Set;

public class ItemUtils {

    private static final Set<Type> usableObjects;

    static {
        usableObjects = Sets.newHashSet();
        usableObjects.add(Type.POTION);
        usableObjects.add(Type.SPELL);
        usableObjects.add(Type.BOTTLE);
        usableObjects.add(Type.FOOD);
        usableObjects.add(Type.MUSICAL);
        usableObjects.add(Type.TELEPORT);
    }

    public static boolean canEquip(Obj obj) {
        return obj instanceof ObjWithClasses;
    }
    
    public static boolean canUse(Obj obj) {
        Type objectType = obj.getType();
        return usableObjects.contains(objectType) || isArrow(obj);
    }

    private static boolean isArrow(Obj obj) {
        return obj.getType().equals(Type.WEAPON) && ((WeaponObj) obj).getKind().equals(WeaponKind.BOW);
    }
}
