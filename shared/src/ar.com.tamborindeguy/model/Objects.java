package ar.com.tamborindeguy.model;

import ar.com.tamborindeguy.objects.types.Obj;
import ar.com.tamborindeguy.objects.types.Type;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Objects {
    private Map<Type, Set<Obj>> objects;
    private Map<Integer, Obj> objectsById;

    public Objects(Map<Type, Set<Obj>> objects, Map<Integer, Obj> objectsById) {
        this.objects = objects;
        this.objectsById = objectsById;
    }

    public Optional<Obj> getObject(int id) {
        return Optional.ofNullable(objectsById.get(id));
    }

    public Optional<Set<Obj>> getTypeObjects(Type type) {
        return Optional.ofNullable(objects.get(type));
    }
}
