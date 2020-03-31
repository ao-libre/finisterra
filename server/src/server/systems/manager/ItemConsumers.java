package server.systems.manager;

import com.artemis.E;
import com.artemis.annotations.Wire;
import entity.character.equipment.Helmet;
import entity.character.equipment.Shield;
import entity.character.equipment.Weapon;
import server.systems.EntityFactorySystem;
import shared.interfaces.Hero;
import shared.interfaces.Race;
import shared.util.EntityUpdateBuilder;
import shared.objects.types.*;

import java.util.function.BiConsumer;

import static com.artemis.E.E;

/**
 * Every item can be used or wear, so here we have how to consume that action
 */
@Wire
public class ItemConsumers extends DefaultManager {

    private WorldManager worldManager;
    public final BiConsumer<Integer, Obj> WEAR = wear();
    private EntityFactorySystem entityFactorySystem;
    public final BiConsumer<Integer, Obj> TAKE_OFF = takeOff();

    public ItemConsumers() {
    }

    private BiConsumer<Integer, Obj> takeOff() {
        return (player, obj) -> {
            E entity = E(player);
            if (obj instanceof WeaponObj) {
                entity.removeWeapon();
                worldManager.notifyUpdate(player, EntityUpdateBuilder.of(player).remove(Weapon.class).build());
            } else if (obj instanceof ArmorObj) {
                Hero hero = Hero.getHeroes().get(entity.getCharHero().heroId);
                entityFactorySystem.setNakedBody(entity, Race.values()[hero.getRaceId()]);
                worldManager.notifyUpdate(player, EntityUpdateBuilder.of(player).withComponents(entity.getBody()).build());
            } else if (obj instanceof HelmetObj) {
                entity.removeHelmet();
                worldManager.notifyUpdate(player, EntityUpdateBuilder.of(player).remove(Helmet.class).build());
            } else if (obj instanceof ShieldObj) {
                entity.removeShield();
                worldManager.notifyUpdate(player, EntityUpdateBuilder.of(player).remove(Shield.class).build());
            }
        };
    }

    private BiConsumer<Integer, Obj> wear() {
        return (player, obj) -> {
            E entity = E(player);
            if (obj instanceof WeaponObj) {
                entity.weaponIndex(obj.getId());
                worldManager.notifyUpdate(player, EntityUpdateBuilder.of(player).withComponents(entity.getWeapon()).build());
            } else if (obj instanceof ArmorObj) {
                entity.bodyIndex(((ArmorObj) obj).getBodyNumber());
                entity.armorIndex(obj.getId());
                worldManager.notifyUpdate(player, EntityUpdateBuilder.of(player).withComponents(entity.getBody(), entity.getArmor()).build());
            } else if (obj instanceof HelmetObj) {
                entity.helmetIndex(obj.getId());
                worldManager.notifyUpdate(player, EntityUpdateBuilder.of(player).withComponents(entity.getHelmet()).build());
            } else if (obj instanceof ShieldObj) {
                entity.shieldIndex(obj.getId());
                worldManager.notifyUpdate(player, EntityUpdateBuilder.of(player).withComponents(entity.getShield()).build());
            }
        };
    }
}
