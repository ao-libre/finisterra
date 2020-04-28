package server.systems.world.entity.npc;

import com.artemis.E;
import com.artemis.annotations.Wire;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import server.systems.config.NPCSystem;
import server.systems.network.ServerSystem;
import server.systems.world.WorldEntitiesSystem;
import shared.model.npcs.NPC;
import shared.util.EntityUpdateBuilder;

import static com.artemis.E.E;

@Wire
public class NPCActionSystem extends PassiveSystem {

    private WorldEntitiesSystem worldEntitiesSystem;
    private ServerSystem serverSystem;
    private NPCSystem npcSystem;

    // TODO refactor, use npc types instead of names
    public void interact(int connectionId, int targetEntity) {
        NPC npc = npcSystem.getNpcs().get(targetEntity);
        int playerId = serverSystem.getPlayerByConnection(connectionId);
        switch (npc.getName()) {
            case "Sacerdote":
                if (E(playerId).healthMin() == 0) {
                    worldEntitiesSystem.resurrect(playerId, true);
                } else {
                    E entity = E(playerId);
                    entity.getHealth().min = entity.getHealth().max;
                    EntityUpdateBuilder resetUpdate = EntityUpdateBuilder.of(playerId);
                    resetUpdate.withComponents(entity.getHealth());
                    worldEntitiesSystem.sendEntityUpdate(playerId, resetUpdate.build());
                }
                break;
        }
    }
}
