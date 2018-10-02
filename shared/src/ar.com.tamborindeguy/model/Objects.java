package ar.com.tamborindeguy.model;

import ar.com.tamborindeguy.objects.types.Obj;
import ar.com.tamborindeguy.objects.types.Type;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Objects {
    private Map<Type, Set<Obj>> objects;
    private Map<Integer, Obj> objectsById;
    // TODO refactor, add id to objects
    private Map<Obj, Integer> idByObject;

    public Objects(Map<Type, Set<Obj>> objects, Map<Integer, Obj> objectsById, Map<Obj, Integer> idByObject) {
        this.objects = objects;
        this.objectsById = objectsById;
        this.idByObject = idByObject;
    }

    public Optional<Obj> getObject(int id) {
        return Optional.ofNullable(objectsById.get(id));
    }

    public Optional<Integer> getObjectId(Obj obj) {
        return Optional.ofNullable(idByObject.get(obj));
    }

    public Optional<Set<Obj>> getTypeObjects(Type type) {
        return Optional.ofNullable(objects.get(type));
    }
}
