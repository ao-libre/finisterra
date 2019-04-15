package server.core;

import com.artemis.FluidEntityPlugin;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import server.combat.CombatSystem;
import server.combat.MagicCombatSystem;
import server.combat.PhysicalCombatSystem;
import server.manager.*;
import server.systems.FootprintSystem;
import server.systems.RandomMovementSystem;
import server.systems.ServerSystem;
import shared.interfaces.Hero;
import shared.model.lobby.Player;
import shared.model.lobby.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.artemis.E.E;

public class Server  {

    private int roomId;
    private final int tcpPort;
    private final int udpPort;
    private ObjectManager objectManager;
    private SpellManager spellManager;
    private World world;
    private Map<Class<? extends IManager>, IManager> managers = new HashMap<>();
    private KryonetServerMarshalStrategy strategy;
    private Set<Player> players;

    public Server(int roomId, int tcpPort, int udpPort, ObjectManager objectManager, SpellManager spellManager) {
        this.roomId = roomId;
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
        this.objectManager = objectManager;
        this.spellManager = spellManager;
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
        initWorld();
        createMap();
        initManagers();
        createWorld();
    }

    private void initManagers() {
        managers.put(NetworkManager.class, new NetworkManager(this, strategy));
        managers.put(ItemManager.class, new ItemManager(this));
        managers.put(MapManager.class, new MapManager(this));
        managers.put(SpellManager.class, spellManager);
        managers.put(ObjectManager.class, objectManager);
        managers.put(WorldManager.class, new WorldManager(this));
        managers.put(PhysicalCombatSystem.class, new PhysicalCombatSystem(this));
        managers.put(MagicCombatSystem.class, new MagicCombatSystem(this));
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
                .with(new FootprintSystem(this, 500))
                .with(new RandomMovementSystem(this));
        world = new World(builder.build());
        System.out.println("WORLD CREATED");
    }


    private void createWorld() {
        // testing
    }

    private void createMap() {

    }

    public void update() {
        world.process();
    }

    public void addPlayers(Set<Player> players) {
        this.players = players;

        players.forEach(player -> {
            // register player

            // create entity
            // notify
        });
    }

    public <T extends IManager> T getManager(Class<T> managerType) {
        return managerType.cast(managers.get(managerType));
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
