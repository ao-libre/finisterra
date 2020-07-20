package server.systems.config;

import com.esotericsoftware.minlog.Log;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import server.database.ServerDescriptorReader;
import shared.model.npcs.NPC;

import java.util.Map;


/**
 * NPC Logic
 */
public class NPCSystem extends PassiveSystem {
    private static final ServerDescriptorReader reader = new ServerDescriptorReader();
    private Map<Integer, NPC> npcs;

    public NPCSystem() {
        init();
    }

    private void init() {
        Log.info("Loading NPCs...");
        npcs = reader.loadNPCs("npcs");
    }

    public Map<Integer, NPC> getNpcs() {
        return npcs;
    }
}
