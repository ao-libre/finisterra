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
import server.systems.ai.NPCRespawnSystem;
import server.systems.ai.PathFindingSystem;
import server.systems.battle.PlayerRespawnSystem;
import server.systems.battle.SpotDominationSystem;
import server.systems.battle.SpotRegenerationSystem;
import server.systems.combat.MagicCombatSystem;
import server.systems.combat.PhysicalCombatSystem;
import server.systems.combat.RangedCombatSystem;
import server.systems.manager.*;
import server.systems.regeneration.RegenerationSystem;

import static server.systems.Intervals.*;

public class Server {

    private final int tcpPort;
    private final int udpPort;
    private int roomId;
    private ObjectManager objectManager;
    private SpellManager spellManager;
    private World world;
    private float tickTime;

    private final static float TICK_RATE = 0.0166f; // 60 ticks per second

    Server(int roomId, int tcpPort, int udpPort, ObjectManager objectManager, SpellManager spellManager) {
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
                .with(new ServerSystem(roomId, new ServerStrategy(tcpPort, udpPort)))
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
                .with(new RegenerationSystem(REGENERATION_INTERVAL))
                .with(new MeditateSystem(MEDITATE_INTERVAL))
                .with(new FootprintSystem(FOOTPRINT_LIVE_TIME))
                .with(new RandomMovementSystem())
                .with(new NPCRespawnSystem())
                .with(new PlayerRespawnSystem())
                .with(new FXSystem())
                .with(new SpotDominationSystem())
                .with(new SpotRegenerationSystem())
                .with(new CommandSystem())
                .with(new BuffSystem());
        world = new World(builder.build());
        System.out.println("World created!");
    }

    public void update() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        tickTime += deltaTime;
        if (tickTime > TICK_RATE) {
            world.setDelta(deltaTime);
            world.process();
            tickTime = tickTime - TICK_RATE;
        }
    }
}
