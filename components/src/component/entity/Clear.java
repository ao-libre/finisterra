package component.entity;

import com.artemis.Component;

/**
 * Componente para la limpieza automática de entidades por timeout.
 * Ver {@link server.systems.world.entity.factory.ClearSystem}
 * Ver {@link game.systems.world.ClearSystem}
 *
 * @todo ¿son necesarios los getters/setters?
 * @todo Evaluar unificar los sistemas de limpieza en cliente y servidor.
 */
public class Clear extends Component {

    float time;

    public Clear() {
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }
}
