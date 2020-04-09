package shared.util;

import com.artemis.Component;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import shared.network.notifications.EntityUpdate;
import shared.network.notifications.RemoveEntity;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static shared.network.notifications.EntityUpdate.NO_ENTITY;

public class EntityUpdateBuilder {

    private EntityUpdate entityUpdate;
    private Map<Class<? extends Component>, Component> components = new HashMap<>();
    private Set<Class<? extends Component>> toRemove = new HashSet<>();

    public static EntityUpdate delete(int entityId) {
        return new RemoveEntity(entityId);
    }

    public static EntityUpdateBuilder of(int entityId) {
        EntityUpdateBuilder builder = new EntityUpdateBuilder();
        builder.entityUpdate = new EntityUpdate();
        builder.entityUpdate.entityId = entityId;
        return builder;
    }

    public static EntityUpdateBuilder none() {
        EntityUpdateBuilder builder = new EntityUpdateBuilder();
        builder.entityUpdate = new EntityUpdate();
        builder.entityUpdate.entityId = NO_ENTITY;
        return builder;
    }

    public static EntityUpdate merge(Set<EntityUpdate> toMerge) {
        int id = toMerge.iterator().next().entityId;
        if (toMerge.stream().anyMatch(RemoveEntity.class::isInstance)) {
            return delete(id);
        }
        EntityUpdateBuilder builder = EntityUpdateBuilder.of(id);
        toMerge.forEach(update -> {
            builder.withComponents(update.components);
            builder.remove(update.toRemove);
        });
        return builder.build();
    }

    public EntityUpdateBuilder withComponents(Component... components) {
        for (Component component : components) {
            // override if exists
            this.components.put(component.getClass(), component);
        }
        return this;
    }

    public EntityUpdateBuilder withComponents(List<Component> components) {
        return withComponents(components.toArray(new Component[0]));
    }

    public EntityUpdateBuilder remove(Class<? extends Component>... toRemove) {
        if(toRemove != null) {
            this.toRemove.addAll(Arrays.asList(toRemove));
        }
        return this;
    }

    public boolean isEmpty() {
        return components.isEmpty() && toRemove.isEmpty();
    }

    public EntityUpdate build() {
        entityUpdate.components = components.values().toArray(new Component[0]);
        entityUpdate.toRemove = toRemove.toArray(new Class[0]);
        return entityUpdate;
    }

    public static EntityUpdate join(EntityUpdate u1, EntityUpdate u2) {
        if (u1 instanceof RemoveEntity) {
            return u1;
        }
        if (u2 instanceof RemoveEntity) {
            return u2;
        }
        // TODO refactor
        // Add new components
        Stream<Component> components = Stream.of(u1.components);
        Set<? extends Class<? extends Component>> classes = components.map(Component::getClass).collect(Collectors.toSet());
        List<Component> newComponents = Lists.newArrayList();
        for (int c = 0; c < u2.components.length; c++) {
            if (!classes.contains(u2.components[c].getClass())) {
                newComponents.add(u2.components[c]);
            }
        }
        List<Component> oldComponents = Lists.newArrayList(u1.components);
        oldComponents.addAll(newComponents);
        u1.components = oldComponents.toArray(new Component[0]);

        // Add new components to remove
        Set<Class> setToRemove = Sets.newHashSet(u1.toRemove);
        setToRemove.addAll(Sets.newHashSet(u2.toRemove));
        u1.toRemove = setToRemove.toArray(new Class[0]);
        return u1;
    }
}
