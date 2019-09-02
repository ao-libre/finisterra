package server.systems.battle;

import com.artemis.E;
import com.artemis.annotations.Wire;
import entity.character.status.Regeneration;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import position.WorldPos;
import server.systems.ServerSystem;
import server.systems.fx.FXSystem;
import server.systems.manager.MapManager;
import server.systems.manager.WorldManager;
import server.utils.WorldUtils;
import shared.interfaces.FXs;
import shared.model.lobby.Player;
import shared.model.lobby.Team;
import shared.network.notifications.RemoveEntity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import static shared.network.notifications.EntityUpdate.EntityUpdateBuilder;

@Wire
public class SpotRegenerationSystem extends PassiveSystem {

    private static final int ZONE_SIZE = 5;
    public static final float REGENERATION_FACTOR = 0.5f;
    private Map<Integer, Set<Integer>> fxs = new HashMap<>();

    private Set<Integer> chaosInSpot = new ConcurrentSkipListSet<>();
    private Set<Integer> realInSpot = new ConcurrentSkipListSet<>();

    private ServerSystem serverSystem;
    private FXSystem fxSystem;
    private WorldManager worldManager;
    private MapManager mapManager;

    public void process(E e) {
        WorldPos worldPos = e.getWorldPos();
        WorldUtils worldUtils = WorldUtils.WorldUtils(getWorld());

        EntityUpdateBuilder update = EntityUpdateBuilder.of(e.id());
        Player player = serverSystem.getLobbyPlayerWithEntityId(e.id());
        Team team = player.getTeam();
        switch (team) {
            case CAOS_ARMY:
                updateRegeneration(e, worldPos, worldUtils, update, Spot.CHAOS.pos, chaosInSpot);
                break;
            case REAL_ARMY:
                updateRegeneration(e, worldPos, worldUtils, update, Spot.REAL.pos, realInSpot);
                break;
        }
    }

    private void updateRegeneration(E e, WorldPos worldPos, WorldUtils worldUtils, EntityUpdateBuilder update, WorldPos spot, Set<Integer> inSpot) {
        int distance = worldUtils.distance(worldPos, spot);
        if (distance < ZONE_SIZE) {
            if (inSpot.contains(e.id())) {
                return;
            }
            inSpot.add(e.id());
            e.regenerationMultiplier(REGENERATION_FACTOR);
            update.withComponents(e.getRegeneration());
            int effectEntityId = fxSystem.attachParticle(e.id(), 4, false);
            int afterEffectEntityId = fxSystem.attachParticle(e.id(), 4, true);
            HashSet<Integer> effects = new HashSet<>();
            effects.add(effectEntityId);
            effects.add(afterEffectEntityId);
            fxs.put(e.id(), effects);
        } else if (inSpot.contains(e.id())) {
            e.removeRegeneration();
            update.remove(Regeneration.class);
            inSpot.remove(e.id());
            Set<Integer> fxE = fxs.get(e.id());
            fxE.forEach(fx -> {
                mapManager.detachEntity(e.id(), fx);
                worldManager.notifyUpdate(e.id(), new RemoveEntity(fx));
            });
        }
        if (!update.isEmpty()) {
            serverSystem.sendToEntity(e.id(), update.build());
        }
    }

    public enum Spot {
        CHAOS(new WorldPos(15, 15, 291)),
        REAL(new WorldPos(30, 30, 292));

        private WorldPos pos;

        Spot(WorldPos pos) {
            this.pos = pos;
        }

        public WorldPos getPos() {
            return pos;
        }
    }
}
