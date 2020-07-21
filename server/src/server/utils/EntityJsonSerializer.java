package server.utils;

import com.artemis.Component;
import com.artemis.annotations.Wire;
import com.esotericsoftware.jsonbeans.Json;
import com.esotericsoftware.jsonbeans.JsonSerializer;
import com.esotericsoftware.jsonbeans.JsonValue;
import component.entity.character.info.Bag;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import server.systems.world.entity.factory.ComponentSystem;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Wire
public class EntityJsonSerializer extends PassiveSystem implements JsonSerializer<Collection<? extends Component>> {

    private final BagJsonSerializer bagJsonSerializer = new BagJsonSerializer();
    private ComponentSystem componentSystem;
    private Map<String, Class<? extends Component>> componentClasses;

    public EntityJsonSerializer() {
        componentClasses = new HashMap<>();
    }

    @Override
    protected void initialize() {
        Collection<Class<? extends Component>> by = componentSystem.getBy(ComponentSystem.Visibility.SERVER);
        by.forEach(clasz -> componentClasses.put(clasz.getName(), clasz));
    }

    @Override
    public void write(Json json, Collection<? extends Component> object, Class knownType) {

        json.writeObjectStart();
        object.forEach(component -> {
            json.writeObjectStart(component.getClass().getName());
            json.writeFields(component);
            json.writeObjectEnd();
        });
        json.writeObjectEnd();
    }

    @Override
    public Collection<? extends Component> read(Json json, JsonValue jsonData, Class type) {
        Collection<Component> allComponents = new HashSet<>();
        for (JsonValue component : jsonData) {
            // read component name
            Class<? extends Component> componentClass = componentClasses.get(component.name);
            try {
                // instantiate component
                Component newComponent = componentClass.getConstructor().newInstance();
                if (newComponent instanceof Bag) {
                    newComponent = bagJsonSerializer.read(json, component, null);
                } else {
                    // read component
                    json.readFields(newComponent, component);
                }

                // add to collection
                allComponents.add(newComponent);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        return allComponents;
    }
}
