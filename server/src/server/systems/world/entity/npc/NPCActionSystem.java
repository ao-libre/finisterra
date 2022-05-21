package server.systems.world.entity.npc;

import com.artemis.ComponentMapper;
import component.entity.character.status.Health;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import server.systems.config.NPCSystem;
import server.systems.network.ServerSystem;
import server.systems.world.WorldEntitiesSystem;
import shared.model.npcs.NPC;
import shared.util.EntityUpdateBuilder;

public class NPCActionSystem extends PassiveSystem {

    private WorldEntitiesSystem worldEntitiesSystem;
    private ServerSystem serverSystem;
    private NPCSystem npcSystem;

    ComponentMapper<Health> mHealth;

    // TODO refactor, use npc types instead of names
    public void interact(int playerId, int targetEntity) {
        NPC npc = npcSystem.getNpcs().get(targetEntity);
        switch (npc.getName()) {
            case "Sacerdote":
                if (mHealth.get(playerId).min == 0) {
                    worldEntitiesSystem.resurrect(playerId, true);
                } else {
                    mHealth.get(playerId).min = mHealth.get(playerId).max;
                    EntityUpdateBuilder resetUpdate = EntityUpdateBuilder.of(playerId);
                    resetUpdate.withComponents(mHealth.get(playerId));
                    worldEntitiesSystem.sendEntityUpdate(playerId, resetUpdate.build());
                }
                break;
        }
    }
}
