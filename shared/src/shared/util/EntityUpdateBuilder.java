package shared.util;

import com.artemis.Component;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import shared.network.notifications.EntityUpdate;
import shared.network.notifications.RemoveEntity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EntityUpdateBuilder {

    private EntityUpdate entityUpdate;
    private Set<Component> components = new HashSet<>();
    private Set<Class> toRemove = new HashSet<>();

    public static EntityUpdate delete(int entityId) {
        return new RemoveEntity(entityId);
    }

    public static EntityUpdateBuilder of(int entityId) {
        EntityUpdateBuilder builder = new EntityUpdateBuilder();
        builder.entityUpdate = new EntityUpdate();
        builder.entityUpdate.entityId = entityId;
        return builder;
    }

    public EntityUpdateBuilder withComponents(Component... components) {
        this.components.addAll(Arrays.asList(components));
        return this;
    }

    public EntityUpdateBuilder withComponents(List<Component> components) {
        return withComponents(components.toArray(new Component[0]));
    }

    public EntityUpdateBuilder remove(Class... toRemove) {
        this.toRemove.addAll(Arrays.asList(toRemove));
        return this;
    }

    public boolean isEmpty() {
        return components.isEmpty() && toRemove.isEmpty();
    }

    public EntityUpdate build() {
        entityUpdate.components = components.toArray(new Component[0]);
        entityUpdate.toRemove = toRemove.toArray(new Class[0]);
        return entityUpdate;
    }

    public static EntityUpdate join(EntityUpdate u1, EntityUpdate u2) {
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
