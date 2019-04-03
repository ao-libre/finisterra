package server.core;

import com.artemis.FluidEntityPlugin;
import com.artemis.SuperMapper;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import server.manager.*;
import server.systems.RandomMovementSystem;
import server.systems.ServerSystem;
import shared.interfaces.Hero;
import shared.model.lobby.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.artemis.E.E;

public class Server  {

    private final int tcpPort;
    private final int udpPort;
    private ObjectManager objectManager;
    private World world;
    private final WorldConfigurationBuilder builder = new WorldConfigurationBuilder();
    private Map<Class<? extends IManager>, IManager> managers = new HashMap<>();
    private KryonetServerMarshalStrategy strategy;
    private Set<Player> players;

    public Server(int tcpPort, int udpPort, ObjectManager objectManager) {
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
        this.objectManager = objectManager;
        create();
    }

    public void create() {
        initWorld();
        createMap();
        initManagers();
        createWorld();
    }

    private void initManagers() {
        managers.put(NetworkManager.class, new NetworkManager(this, strategy));
        managers.put(CombatManager.class, new CombatManager(this));
        managers.put(ItemManager.class, new ItemManager(this));
        managers.put(SpellManager.class, new SpellManager(this));
        managers.put(MapManager.class, new MapManager(this));
        managers.put(ObjectManager.class, objectManager);
        managers.put(WorldManager.class, new WorldManager(this));
    }

    public World getWorld() {
        return world;
    }

    private void initWorld() {
        System.out.println("Initializing systems...");

        strategy = new KryonetServerMarshalStrategy(tcpPort, udpPort);
        builder
                .with(new FluidEntityPlugin())
                .with(new ServerSystem(this, strategy))
                .with(new RandomMovementSystem(this));
        world = new World(builder.build());
        System.out.println("WORLD CREATED");
    }


    private void createWorld() {
        // testing
        int player2 = getWorldManager().createEntity("guidota2", Hero.WARRIOR.ordinal());
        E(player2).randomMovement();
        getMapManager().updateEntity(player2);
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

    public CombatManager getCombatManager() {
        return getManager(CombatManager.class);
    }

    public ObjectManager getObjectManager() {
        return getManager(ObjectManager.class);
    }
}
