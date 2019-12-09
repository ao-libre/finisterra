package server.core;

import com.artemis.FluidEntityPlugin;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.minlog.Log;
import server.network.ServerNotificationProcessor;
import server.network.ServerRequestProcessor;
import server.systems.*;
import server.systems.ai.NPCAttackSystem;
import server.systems.ai.PathFindingSystem;
import server.systems.ai.RespawnSystem;
import server.systems.combat.MagicCombatSystem;
import server.systems.combat.PhysicalCombatSystem;
import server.systems.combat.RangedCombatSystem;
import server.systems.manager.*;
import shared.model.lobby.Player;
import shared.model.map.Map;

import java.util.HashMap;
import java.util.Set;

import static server.systems.Intervals.*;

public class Server {

    private final int tcpPort;
    private final int udpPort;
    private int roomId;
    private ObjectManager objectManager;
    private SpellManager spellManager;
    private World world;

    public Server(int roomId, int tcpPort, int udpPort, ObjectManager objectManager, SpellManager spellManager, HashMap<Integer, Map> maps) {
        this.roomId = roomId;
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
        this.objectManager = objectManager;
        this.spellManager = spellManager;
        create();
    }

    int getTcpPort() {
        return tcpPort;
    }

    int getUdpPort() {
        return udpPort;
    }

    int getRoomId() {
        return roomId;
    }

    private void create() {
        long start = System.currentTimeMillis();
        initWorld();
        Log.info("Server initialization", "Elapsed time: " + (start - System.currentTimeMillis()));
    }

    public World getWorld() {
        return world;
    }

    private void initWorld() {
        Log.info("Initializing systems...");
        final WorldConfigurationBuilder builder = new WorldConfigurationBuilder();
        ServerStrategy strategy = new ServerStrategy(tcpPort, udpPort);
        builder
                .with(new FluidEntityPlugin())
                .with(new ServerSystem(strategy))
                .with(new ServerNotificationProcessor())
                .with(new ServerRequestProcessor())
                .with(new EntityFactorySystem())
                .with(new ItemManager())
                .with(new ItemConsumers())
                .with(new NPCManager())
                .with(new MapManager())
                .with(spellManager)
                .with(objectManager)
                .with(new WorldManager())
                .with(new PhysicalCombatSystem())
                .with(new RangedCombatSystem())
                .with(new CharacterTrainingSystem())
                .with(new MagicCombatSystem())
                .with(new PathFindingSystem(PATH_FINDING_INTERVAL))
                .with(new NPCAttackSystem(NPC_ATTACK_INTERVAL))
                .with(new EnergyRegenerationSystem(ENERGY_REGENERATION_INTERVAL))
                .with(new MeditateSystem(MEDITATE_INTERVAL))
                .with(new FootprintSystem(FOOTPRINT_LIVE_TIME))
                .with(new RandomMovementSystem())
                .with(new RespawnSystem())
                .with(new BuffSystem())
                .with(new CommandSystem());
        world = new World(builder.build());
        Log.info("World created successfully!");
    }

    public void update() {
        world.setDelta(MathUtils.clamp(Gdx.graphics.getDeltaTime(), 0, 1 / 16f));
        world.process();
    }

    void addPlayers(Set<Player> players) {
        // TODO
    }
}
