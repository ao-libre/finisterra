package server.manager;

import com.artemis.E;
import entity.character.equipment.Helmet;
import entity.character.equipment.Shield;
import entity.character.equipment.Weapon;
import server.core.Server;
import shared.interfaces.Hero;
import shared.interfaces.Race;
import shared.network.notifications.EntityUpdate.EntityUpdateBuilder;
import shared.objects.types.*;

import java.util.function.BiConsumer;

import static com.artemis.E.E;

/**
 * Every item can be used or wear, so here we have how to consume that action
 */
public class ItemConsumers {

    private Server server;
    public final BiConsumer<Integer, Obj> WEAR = wear();
    public final BiConsumer<Integer, Obj> TAKE_OFF = takeOff();

    public ItemConsumers(Server server) {
        this.server = server;
    }

    public Server getServer() {
        return server;
    }

    private BiConsumer<Integer, Obj> takeOff() {
        return (player, obj) -> {
            E entity = E(player);
            if (obj instanceof WeaponObj) {
                entity.removeWeapon();
                getServer().getWorldManager().notifyUpdate(player, EntityUpdateBuilder.of(player).remove(Weapon.class).build());
            } else if (obj instanceof ArmorObj) {
                Hero hero = Hero.getHeroes().get(entity.getCharHero().heroId);
                getServer().getWorldManager().setNakedBody(entity, Race.values()[hero.getRaceId()]);
                getServer().getWorldManager().notifyUpdate(player, EntityUpdateBuilder.of(player).withComponents(entity.getBody()).build());
            } else if (obj instanceof HelmetObj) {
                entity.removeHelmet();
                getServer().getWorldManager().notifyUpdate(player, EntityUpdateBuilder.of(player).remove(Helmet.class).build());
            } else if (obj instanceof ShieldObj) {
                entity.removeShield();
                getServer().getWorldManager().notifyUpdate(player, EntityUpdateBuilder.of(player).remove(Shield.class).build());
            }
        };
    }

    private BiConsumer<Integer, Obj> wear() {
        return (player, obj) -> {
            E entity = E(player);
            if (obj instanceof WeaponObj) {
                entity.weaponIndex(obj.getId());
                getServer().getWorldManager().notifyUpdate(player, EntityUpdateBuilder.of(player).withComponents(entity.getWeapon()).build());
            } else if (obj instanceof ArmorObj) {
                entity.bodyIndex(((ArmorObj) obj).getBodyNumber());
                entity.armorIndex(obj.getId());
                getServer().getWorldManager().notifyUpdate(player, EntityUpdateBuilder.of(player).withComponents(entity.getBody(), entity.getArmor()).build());
            } else if (obj instanceof HelmetObj) {
                entity.helmetIndex(obj.getId());
                getServer().getWorldManager().notifyUpdate(player, EntityUpdateBuilder.of(player).withComponents(entity.getHelmet()).build());
            } else if (obj instanceof ShieldObj) {
                entity.shieldIndex(obj.getId());
                getServer().getWorldManager().notifyUpdate(player, EntityUpdateBuilder.of(player).withComponents(entity.getShield()).build());
            }
        };
    }
}
