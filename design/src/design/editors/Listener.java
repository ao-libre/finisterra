package design.editors;

import model.ID;

public interface Listener<T extends ID> {

    void select(T t);

}
