package server.systems.battle;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.FluidIteratingSystem;
import com.artemis.annotations.Wire;
import entity.character.Character;
import entity.npc.Respawn;
import server.systems.EntityFactorySystem;
import server.systems.ServerSystem;
import server.systems.manager.MapManager;
import server.systems.manager.WorldManager;
import shared.model.lobby.Player;

@Wire
public class PlayerRespawnSystem extends FluidIteratingSystem {

    private ServerSystem serverSystem;
    private EntityFactorySystem entityFactorySystem;
    private WorldManager worldManager;
    private MapManager mapManager;

    public PlayerRespawnSystem() {
        super(Aspect.all(Respawn.class, Character.class));
    }

    @Override
    protected void process(E e) {
        Respawn respawn = e.getRespawn();
        float respawnTime = Math.max(0, respawn.getTime() - world.getDelta());
        respawn.setTime(respawnTime);
        if (respawnTime <= 0) {
            int connectionId = serverSystem.getConnectionByPlayer(e.id());
            Player lobbyPlayer = serverSystem.getLobbyPlayer(connectionId);

            // remove old entity
            mapManager.removeEntity(e.id());
            worldManager.sendEntityRemove(e.id(), e.id());
            e.deleteFromWorld();
            serverSystem.unregisterUserConnection(e.id());

            // create again
            worldManager.login(connectionId, lobbyPlayer);
        }

    }
}
