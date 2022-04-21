package server.systems.world;

import com.artemis.annotations.All;
import com.artemis.systems.IntervalIteratingSystem;
import component.entity.character.Character;
import component.entity.character.info.Name;
import server.systems.account.UserSystem;

@All({Character.class, Name.class}) // Nos suscribimos a solamente a PJs
public class WorldSaveSystem extends IntervalIteratingSystem {

    private UserSystem userSystem;

    public WorldSaveSystem(float interval) {
        // Acá hay un tema de API de Artemis... Que debería proveer un constructor que tome solo el intervalo
        // @todo: Abrir un issue upstream
        super(null, interval);
    }

    @Override
    protected void process(int entityId) {
        // @todo: ¿Por qué el guardado está en otro sistema, si este es el sistema de guardado?
        userSystem.save(entityId, () -> {}); // @todo: Que el runnable sea opcional o algo así
    }
}
