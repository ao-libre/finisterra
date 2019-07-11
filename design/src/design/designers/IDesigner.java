package design.designers;

import java.util.Map;
import java.util.Optional;

public interface IDesigner<T, P extends IDesigner.Parameters<T>> {

    void load(P params);

    void save();

    Map<Integer, T> get();

    Optional<T> get(int id);

    T create();

    void modify(T element);

    void delete(T element);

    interface Parameters<T> {}
}
