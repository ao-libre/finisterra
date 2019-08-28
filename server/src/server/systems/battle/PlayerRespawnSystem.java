package server.systems.battle;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.FluidIteratingSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import entity.character.Character;
import entity.npc.Respawn;
import server.core.Finisterra;
import server.core.Server;
import server.systems.EntityFactorySystem;
import server.systems.FinisterraSystem;
import server.systems.ServerSystem;
import server.systems.manager.MapManager;
import server.systems.manager.WorldManager;
import shared.model.lobby.Player;
import shared.model.lobby.Room;

import java.util.Optional;

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
            // Respawn player
            mapManager.removeEntity(e.id());
            Finisterra finisterra = (Finisterra) Gdx.app.getApplicationListener();
            int connectionId = serverSystem.getConnectionByPlayer(e.id());
            Player lobbyPlayer = serverSystem.getLobbyPlayer(connectionId);
            entityFactorySystem.resetPlayer(e, lobbyPlayer.getTeam());
            mapManager.updateEntity(e.id());
        }

    }
}
