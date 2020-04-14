package server.systems.manager;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.utils.Bag;
import com.google.common.collect.Sets;
import component.entity.Clear;
import component.entity.character.attributes.*;
import component.entity.character.info.CharHero;
import component.entity.character.info.Gold;
import component.entity.character.info.SpellBook;
import component.entity.character.states.*;
import component.entity.character.status.*;
import component.entity.combat.AttackPower;
import component.entity.combat.EvasionPower;
import component.entity.npc.*;
import component.entity.world.Footprint;
import component.physics.AttackInterval;
import component.physics.UseInterval;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import org.reflections.Reflections;

import java.util.*;

@Wire
public class ComponentManager extends PassiveSystem {

    Map<Visibility, Set<Class<? extends Component>>> componentsByVisibility;

    public ComponentManager() {
        componentsByVisibility = new HashMap<>();
        Reflections reflections = new Reflections("component");
        Set<Class<? extends Component>> allClasses = reflections.getSubTypesOf(Component.class);
        componentsByVisibility.put(Visibility.SERVER, allClasses);

        // remove server only components
        allClasses.remove(Clear.class);
        allClasses.remove(AIMovement.class);
        allClasses.remove(Attackable.class);
        allClasses.remove(Hostile.class);
        allClasses.remove(OriginPos.class);
        allClasses.remove(Respawn.class);
        allClasses.remove(Footprint.class);
        allClasses.remove(AttackInterval.class);
        allClasses.remove(UseInterval.class);

        componentsByVisibility.put(Visibility.CLIENT_ALL, Sets.newHashSet(allClasses));

        // remove not shareable components
        allClasses.remove(Agility.class);
        allClasses.remove(Charisma.class);
        allClasses.remove(Constitution.class);
        allClasses.remove(Intelligence.class);
        allClasses.remove(Strength.class);
        allClasses.remove(component.entity.character.info.Bag.class);
        allClasses.remove(CharHero.class);
        allClasses.remove(Gold.class);
        allClasses.remove(SpellBook.class);
        allClasses.remove(Buff.class);
        allClasses.remove(CanWrite.class);
        allClasses.remove(Meditating.class);
        allClasses.remove(Navigating.class);
        allClasses.remove(Resting.class);
        allClasses.remove(Writing.class);
        allClasses.remove(Hit.class);
        allClasses.remove(Hungry.class);
        allClasses.remove(Level.class);
        allClasses.remove(Stamina.class);
        allClasses.remove(Thirst.class);
        allClasses.remove(AttackPower.class);
        allClasses.remove(EvasionPower.class);
        allClasses.remove(AttackPower.class);
        componentsByVisibility.put(Visibility.CLIENT_PUBLIC, Sets.newHashSet(allClasses));
    }

    public List<Component> getComponents(int entityId, Visibility visibility) {
        Entity entity = world.getEntity(entityId);
        Bag<Component> components = entity.getComponents(new Bag<>());
        List<Component> filteredComponents = new ArrayList<>();
        components.forEach(component -> {
            if (satisfies(visibility, component)) {
                filteredComponents.add(component);
            }
        });
        return filteredComponents;
    }

    private boolean satisfies(Visibility visibility, Component component) {
        return componentsByVisibility.get(visibility).contains(component.getClass());
    }

    public enum Visibility {
        CLIENT_PUBLIC,
        CLIENT_ALL,
        SERVER
    }
}
