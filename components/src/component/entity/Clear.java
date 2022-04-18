package component.entity;

import com.artemis.Component;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Componente para la limpieza automática de entidades por timeout.
 * Ver {@link server.systems.world.entity.factory.ClearSystem}
 * Ver {@link game.systems.world.ClearSystem}
 *
 * @todo ¿son necesarios los getters/setters?
 * @todo Evaluar unificar los sistemas de limpieza en cliente y servidor.
 */

@Getter
@Setter
@NoArgsConstructor
public class Clear extends Component {

    private float time;

}
