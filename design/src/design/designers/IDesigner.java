package design.designers;

import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.List;
import java.util.Optional;

public interface IDesigner<T, P extends IDesigner.Parameters<T>> {

    default void load(P params) {

    }

    void reload();

    void save();

    List<T> get();

    Optional<T> get(int id);

    T create();

    void modify(T element, Stage stage);

    void delete(T element);

    interface Parameters<T> {}
}
