package design.editors.fields;

import model.ID;

public interface Listener<T extends ID> {

    void select(T t);

}
