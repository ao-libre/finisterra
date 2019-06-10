package server.core;

import com.artemis.BaseSystem;
import com.artemis.FluidEntityPlugin;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import server.combat.CombatSystem;
import server.combat.MagicCombatSystem;
import server.combat.PhysicalCombatSystem;
import server.systems.*;
import server.systems.ai.NPCAttackSystem;
import server.systems.ai.PathFindingSystem;
import server.systems.ai.RespawnSystem;
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
    private HashMap<Integer, Map> maps;
    private World world;
    private KryonetServerMarshalStrategy strategy;
    private Set<Player> players;

    public Server(int roomId, int tcpPort, int udpPort, ObjectManager objectManager, SpellManager spellManager, HashMap<Integer, Map> maps) {
        this.roomId = roomId;
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
        this.objectManager = objectManager;
        this.spellManager = spellManager;
        this.maps = maps;
        create();
    }

    public int getTcpPort() {
        return tcpPort;
    }

    public int getUdpPort() {
        return udpPort;
    }

    public int getRoomId() {
        return roomId;
    }

    public void create() {
        long start = System.currentTimeMillis();
        initWorld();
        createMap();
        createWorld();
        Gdx.app.log("Server initialization", "Elapsed time: " + (start - System.currentTimeMillis()));
    }

    public World getWorld() {
        return world;
    }

    private void initWorld() {
        System.out.println("Initializing systems...");
        final WorldConfigurationBuilder builder = new WorldConfigurationBuilder();
        strategy = new KryonetServerMarshalStrategy(tcpPort, udpPort);
        builder
                .with(new FluidEntityPlugin())
                .with(new ServerSystem(this, strategy))
                .with(new NetworkManager(this, strategy))
                .with(new ItemManager(this))
                .with(new NPCManager())
                .with(new MapManager(this, maps))
                .with(spellManager)
                .with(objectManager)
                .with(new PathFindingSystem(PATH_FINDING_INTERVAL))
                .with(new NPCAttackSystem(NPC_ATTACK_INTERVAL))
                .with(new WorldManager(this))
                .with(new PhysicalCombatSystem(this))
                .with(new MagicCombatSystem(this))
                .with(new EnergyRegenerationSystem(ENERGY_REGENERATION_INTERVAL))
                .with(new MeditateSystem(this, MEDITATE_INTERVAL))
                .with(new FootprintSystem(this, FOOTPRINT_LIVE_TIME))
                .with(new RandomMovementSystem(this))
                .with(new RespawnSystem())
                .with(new BuffSystem());
        world = new World(builder.build());
        world.getSystem(MapManager.class).postInitialize();
        System.out.println("WORLD CREATED");
    }


    private void createWorld() {
        // testing
    }

    private void createMap() {

    }

    public void update() {
        world.setDelta(MathUtils.clamp(Gdx.graphics.getDeltaTime(), 0, 1 / 16f));
        world.process();
    }

    void addPlayers(Set<Player> players) {
        this.players = players;
    }

    private <T extends BaseSystem> T getManager(Class<T> managerType) {
        return world.getSystem(managerType);
    }

    public ItemManager getItemManager() {
        return getManager(ItemManager.class);
    }

    public MapManager getMapManager() {
        return getManager(MapManager.class);
    }

    public WorldManager getWorldManager() {
        return getManager(WorldManager.class);
    }

    public NetworkManager getNetworkManager() {
        return getManager(NetworkManager.class);
    }

    public SpellManager getSpellManager() {
        return getManager(SpellManager.class);
    }

    public CombatSystem getCombatManager() {
        return getManager(PhysicalCombatSystem.class);
    }

    public MagicCombatSystem getMagicCombatManager() {
        return getManager(MagicCombatSystem.class);
    }

    public ObjectManager getObjectManager() {
        return getManager(ObjectManager.class);
    }
}
