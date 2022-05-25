package server.systems.world.entity.item;

import com.artemis.Component;
import com.artemis.ComponentMapper;
import component.entity.character.equipment.Armor;
import component.entity.character.equipment.Helmet;
import component.entity.character.equipment.Shield;
import component.entity.character.equipment.Weapon;
import component.entity.character.info.CharHero;
import component.entity.character.parts.Body;
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

/**
 * Every item can be used or wear, so here we have how to consume that action
 */
public class ItemUsageSystem extends PassiveSystem {

    private WorldEntitiesSystem worldEntitiesSystem;
    private EntityUpdateSystem entityUpdateSystem;
    public final BiConsumer<Integer, Obj> WEAR = wear();
    private EntityFactorySystem entityFactorySystem;
    public final BiConsumer<Integer, Obj> TAKE_OFF = takeOff();

    ComponentMapper<Weapon> mWeapon;
    ComponentMapper<CharHero> mCharHero;
    ComponentMapper<Body> mBody;
    ComponentMapper<Armor> mArmor;
    ComponentMapper<Helmet> mHelmet;
    ComponentMapper<Shield> mShield;

    public ItemUsageSystem() {
    }

    private BiConsumer<Integer, Obj> takeOff() {
        return (playerId, obj) -> {
            if (obj instanceof WeaponObj) {
                mWeapon.remove(playerId);
                remove(playerId, Weapon.class);
            } else if (obj instanceof ArmorObj) {
                Hero hero = Hero.getHeroes().get(mCharHero.get(playerId).heroId);
                entityFactorySystem.setNakedBody(playerId, Race.values()[hero.getRaceId()]);
                update(playerId, mBody.get(playerId));
                // @todo ¿no hay que desequipar la armadura y hacer el update?
            } else if (obj instanceof HelmetObj) {
                mHelmet.remove(playerId);
                remove(playerId, Helmet.class);
            } else if (obj instanceof ShieldObj) {
                mShield.remove(playerId);
                remove(playerId, Shield.class);
            }
        };
    }

    private BiConsumer<Integer, Obj> wear() {
        return (playerId, obj) -> {
            if (obj instanceof WeaponObj) {
                mWeapon.create(playerId).setIndex(obj.getId());
                update(playerId, mWeapon.get(playerId));
            } else if (obj instanceof ArmorObj) {
                mBody.get(playerId).setIndex(((ArmorObj) obj).getBodyNumber());
                mArmor.create(playerId).setIndex(obj.getId());
                update(playerId, mBody.get(playerId));
                // @todo ¿no hay que enviar el update de armadura?
            } else if (obj instanceof HelmetObj) {
                mHelmet.create(playerId).setIndex(obj.getId());
                update(playerId, mHelmet.get(playerId));
            } else if (obj instanceof ShieldObj) {
                mShield.create(playerId).setIndex(obj.getId());
                update(playerId, mShield.get(playerId));
            }
        };
    }

    private void update(int playerId, Component component) {
        EntityUpdate update = EntityUpdateBuilder.of(playerId).withComponents(component).build();
        entityUpdateSystem.add(update, UpdateTo.ALL);
    }

    private void remove(int playerId, Class<? extends Component> componentClass) {
        EntityUpdate update = EntityUpdateBuilder.of(playerId).remove(componentClass).build();
        entityUpdateSystem.add(update, UpdateTo.ALL);
    }
}
