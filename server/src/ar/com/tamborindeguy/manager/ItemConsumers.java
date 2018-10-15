package ar.com.tamborindeguy.manager;

import ar.com.tamborindeguy.network.notifications.EntityUpdate;
import ar.com.tamborindeguy.objects.types.*;
import com.artemis.Component;
import com.artemis.E;
import entity.Helmet;
import entity.Shield;
import entity.Weapon;

import java.util.function.BiConsumer;

import static com.artemis.E.E;

public class ItemConsumers {

    public static BiConsumer<Integer, Obj> getEquipConsumer(boolean equipped) {
        if (equipped) {
            return equip();
        }
        return desequip();
    }

    private static BiConsumer<Integer, Obj> desequip() {
        return (player, obj) -> {
            E entity = E(player);
            if (obj instanceof WeaponObj) {
                entity.removeWeapon();
                WorldManager.notifyUpdate(player, new EntityUpdate(player, new Component[0], new Class[] {Weapon.class}));
            } else if (obj instanceof ArmorObj) {
                entity.bodyIndex(((ArmorObj) obj).getBodyNumber()); // TODO change to raza body
                WorldManager.notifyUpdate(player, new EntityUpdate(player, new Component[]{entity.getBody()}, new Class[0]));
            } else if (obj instanceof HelmetObj) {
                entity.removeHelmet();
                WorldManager.notifyUpdate(player, new EntityUpdate(player, new Component[0], new Class[] {Helmet.class}));
            } else if (obj instanceof ShieldObj) {
                entity.removeShield();
                WorldManager.notifyUpdate(player, new EntityUpdate(player, new Component[0], new Class[] {Shield.class}));
            }
        };
    }

    private static BiConsumer<Integer, Obj> equip() {
        return (player, obj) -> {
            E entity = E(player);
            if (obj instanceof WeaponObj) {
                entity.weaponIndex(((WeaponObj) obj).getAnimationId());
                WorldManager.notifyUpdate(player, new EntityUpdate(player, new Component[]{entity.getWeapon()}, new Class[0]));
            } else if (obj instanceof ArmorObj) {
                entity.bodyIndex(((ArmorObj) obj).getBodyNumber());
                WorldManager.notifyUpdate(player, new EntityUpdate(player, new Component[]{entity.getBody()}, new Class[0]));
            } else if (obj instanceof HelmetObj) {
                entity.helmetIndex(((HelmetObj) obj).getAnimationId());
                WorldManager.notifyUpdate(player, new EntityUpdate(player, new Component[]{entity.getHelmet()}, new Class[0]));
            } else if (obj instanceof ShieldObj) {
                entity.shieldIndex(((ShieldObj) obj).getAnimationId());
                WorldManager.notifyUpdate(player, new EntityUpdate(player, new Component[]{entity.getShield()}, new Class[0]));
            }
        };
    }
}
