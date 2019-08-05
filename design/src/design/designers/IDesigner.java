package design.designers;

import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.Map;
import java.util.Optional;

public interface IDesigner<T, P extends IDesigner.Parameters<T>> {

    default void load(P params) {
    }

    void reload();

    void save();

    Map<Integer, T> get();

    Optional<T> get(int id);

    Optional<T> create();

    void modify(T element, Stage stage);

    void delete(T element);

    void add(T t);

    boolean contains(int id);

    void markUsedImages();

    interface Parameters<T> {
    }
}
