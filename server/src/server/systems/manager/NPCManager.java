package server.systems.manager;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.esotericsoftware.minlog.Log;
import server.database.ServerDescriptorReader;
import shared.model.Spell;
import shared.model.npcs.NPC;
import shared.model.readers.DescriptorsReader;
import shared.util.SharedResources;
import shared.util.SpellJson;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


/**
 * NPC Logic
 */
public class NPCManager extends BaseSystem {
    private static ServerDescriptorReader reader = new ServerDescriptorReader();
    private Map<Integer, NPC> npcs;

    public NPCManager() {
        init();
    }

    @Override
    protected void processSystem() {}

    public void init() {
        Log.info("Loading NPCs...");
        npcs = reader.loadNPCs("npcs");
    }

    public Map<Integer, NPC> getNpcs() {
        return npcs;
    }
}
