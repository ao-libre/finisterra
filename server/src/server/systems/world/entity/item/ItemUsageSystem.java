package server.systems.world.entity.item;

import com.artemis.Component;
import com.artemis.E;
import com.artemis.annotations.Wire;
import component.entity.character.equipment.Helmet;
import component.entity.character.equipment.Shield;
import component.entity.character.equipment.Weapon;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import server.systems.network.EntityUpdateSystem;
import server.systems.world.WorldEntitiesSystem;
import server.systems.world.entity.factory.EntityFactorySystem;
import server.utils.UpdateTo;
import shared.interfaces.Hero;
import shared.interfaces.Race;
import shared.network.notifications.EntityUpdate;
import shared.objects.types.*;
import shared.util.EntityUpdateBuilder;

import java.util.function.BiConsumer;

import static com.artemis.E.E;

/**
 * Every item can be used or wear, so here we have how to consume that action
 */
@Wire
public class ItemUsageSystem extends PassiveSystem {

    private WorldEntitiesSystem worldEntitiesSystem;
    private EntityUpdateSystem entityUpdateSystem;
    public final BiConsumer<Integer, Obj> WEAR = wear();
    private EntityFactorySystem entityFactorySystem;
    public final BiConsumer<Integer, Obj> TAKE_OFF = takeOff();

    public ItemUsageSystem() {
    }

    private BiConsumer<Integer, Obj> takeOff() {
        return (player, obj) -> {
            E entity = E(player);
            if (obj instanceof WeaponObj) {
                entity.removeWeapon();
                remove(player, Weapon.class);
            } else if (obj instanceof ArmorObj) {
                Hero hero = Hero.getHeroes().get(entity.getCharHero().heroId);
                entityFactorySystem.setNakedBody(entity, Race.values()[hero.getRaceId()]);
                update(player, entity.getBody());
            } else if (obj instanceof HelmetObj) {
                entity.removeHelmet();
                remove(player, Helmet.class);
            } else if (obj instanceof ShieldObj) {
                entity.removeShield();
                remove(player, Shield.class);
            }
        };
    }

    private BiConsumer<Integer, Obj> wear() {
        return (player, obj) -> {
            E entity = E(player);
            if (obj instanceof WeaponObj) {
                entity.weaponIndex(obj.getId());
                update(player, entity.getWeapon());
            } else if (obj instanceof ArmorObj) {
                entity.bodyIndex(((ArmorObj) obj).getBodyNumber());
                entity.armorIndex(obj.getId());
                update(player, entity.getBody());
            } else if (obj instanceof HelmetObj) {
                entity.helmetIndex(obj.getId());
                update(player, entity.getHelmet());
            } else if (obj instanceof ShieldObj) {
                entity.shieldIndex(obj.getId());
                update(player, entity.getShield());
            }
        };
    }

    private void update(int user, Component component) {
        EntityUpdate update = EntityUpdateBuilder.of(user).withComponents(component).build();
        entityUpdateSystem.add(update, UpdateTo.ALL);
    }

    private void remove(int user, Class clasz) {
        EntityUpdate update = EntityUpdateBuilder.of(user).remove(clasz).build();
        entityUpdateSystem.add(update, UpdateTo.ALL);
    }
}
