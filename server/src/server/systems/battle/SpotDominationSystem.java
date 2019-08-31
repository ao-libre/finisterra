package server.systems.battle;

import battle.Gems;
import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.E;
import com.artemis.EBag;
import com.artemis.annotations.Wire;
import entity.character.Character;
import position.WorldPos;
import server.systems.ServerSystem;
import server.utils.WorldUtils;
import shared.model.lobby.Player;
import shared.model.lobby.Team;
import shared.network.battle.DominationNotification;

import java.util.HashSet;
import java.util.Set;

import static shared.network.battle.DominationNotification.TIME_TO_DOMINATE;

@Wire
public class SpotDominationSystem extends BaseSystem {

    private static final int ZONE_SIZE = 15;
    private ServerSystem serverSystem;

    private static final WorldPos SPOT = new WorldPos(290, 50, 50);

    private Team dominating = Team.NO_TEAM;
    private float dominatingTime;

    @Override
    protected void processSystem() {
        EBag players = E.withAspect(Aspect.all(Character.class, Gems.class, WorldPos.class));

        Set<Integer> blueTeam = getTeam(players, Team.REAL_ARMY);
        Set<Integer> redTeam = getTeam(players, Team.CAOS_ARMY);

        Team previusDominating = dominating;
        // TODO discuss strategy to dominate: maybe more players involves dominating or maybe first team keeps dominating
        switch (dominating) {
            case NO_TEAM:
                if (!blueTeam.isEmpty() && !redTeam.isEmpty()) {
                    // do nothing
                    return;
                } else if (!blueTeam.isEmpty()) {
                    dominating = Team.REAL_ARMY;
                } else if (!redTeam.isEmpty()) {
                    dominating = Team.CAOS_ARMY;
                }
                break;
            case REAL_ARMY:
                if (blueTeam.isEmpty()) {
                    dominating = redTeam.isEmpty() ? Team.NO_TEAM : Team.CAOS_ARMY;
                }
                break;
            case CAOS_ARMY:
                if (redTeam.isEmpty()) {
                    dominating = blueTeam.isEmpty() ? Team.NO_TEAM : Team.REAL_ARMY;
                }
                break;
        }

        boolean dominatingChanged = previusDominating != dominating;
        if (dominatingChanged) {
            dominatingTime = 0;
            // notify all
            players.forEach(this::notify);
        } else {
            dominatingTime += world.getDelta();
        }

        if (dominatingTime >= TIME_TO_DOMINATE) {
            // TODO DOMINADO!
        }
    }

    private void notify(E entity) {
        int connectionByPlayer = serverSystem.getConnectionByPlayer(entity.id());
        serverSystem.sendTo(connectionByPlayer, new DominationNotification(dominating));
    }

    private Set<Integer> getTeam(EBag players, Team team) {
        Set<Integer> result = new HashSet<>();
        players.iterator().forEachRemaining(it -> {
            Player player = serverSystem.getLobbyPlayerWithEntityId(it.id());
            if (player.getTeam().equals(team)) {
                int distance = WorldUtils.WorldUtils(getWorld()).distance(SPOT, it.getWorldPos());
                if (distance < ZONE_SIZE) {
                    result.add(it.id());
                }
            }
        });
        return result;
    }
}
