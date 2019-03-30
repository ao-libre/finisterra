package server.manager;

import com.artemis.Component;
import com.artemis.E;
import entity.Helmet;
import entity.Shield;
import entity.Weapon;
import server.core.Server;
import shared.interfaces.Hero;
import shared.interfaces.Race;
import shared.network.notifications.EntityUpdate;
import shared.objects.types.*;

import java.util.function.BiConsumer;

import static com.artemis.E.E;

/**
 * Every item can be used or wear, so here we have how to consume that action
 */
public class ItemConsumers {

    public final BiConsumer<Integer, Obj> WEAR = wear();
    public final BiConsumer<Integer, Obj> TAKE_OFF = takeOff();
    private Server server;

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
                getServer().getWorldManager().notifyUpdate(player, new EntityUpdate(player, new Component[0], new Class[] {Weapon.class}));
            } else if (obj instanceof ArmorObj) {
                Hero hero = Hero.values()[entity.getCharHero().heroId];
                getServer().getWorldManager().setNakedBody(entity, Race.values()[hero.getRaceId()]);
                getServer().getWorldManager().notifyUpdate(player, new EntityUpdate(player, new Component[]{entity.getBody()}, new Class[0]));
            } else if (obj instanceof HelmetObj) {
                entity.removeHelmet();
                getServer().getWorldManager().notifyUpdate(player, new EntityUpdate(player, new Component[0], new Class[] {Helmet.class}));
            } else if (obj instanceof ShieldObj) {
                entity.removeShield();
                getServer().getWorldManager().notifyUpdate(player, new EntityUpdate(player, new Component[0], new Class[] {Shield.class}));
            }
        };
    }

    private BiConsumer<Integer, Obj> wear() {
        return (player, obj) -> {
            E entity = E(player);
            if (obj instanceof WeaponObj) {
                entity.weaponIndex(((WeaponObj) obj).getAnimationId());
                getServer().getWorldManager().notifyUpdate(player, new EntityUpdate(player, new Component[]{entity.getWeapon()}, new Class[0]));
            } else if (obj instanceof ArmorObj) {
                entity.bodyIndex(((ArmorObj) obj).getBodyNumber());
                getServer().getWorldManager().notifyUpdate(player, new EntityUpdate(player, new Component[]{entity.getBody()}, new Class[0]));
            } else if (obj instanceof HelmetObj) {
                entity.helmetIndex(((HelmetObj) obj).getAnimationId());
                getServer().getWorldManager().notifyUpdate(player, new EntityUpdate(player, new Component[]{entity.getHelmet()}, new Class[0]));
            } else if (obj instanceof ShieldObj) {
                entity.shieldIndex(((ShieldObj) obj).getAnimationId());
                getServer().getWorldManager().notifyUpdate(player, new EntityUpdate(player, new Component[]{entity.getShield()}, new Class[0]));
            }
        };
    }
}
