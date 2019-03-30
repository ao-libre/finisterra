package server.core;

import com.artemis.*;
import com.badlogic.gdx.Gdx;
import network.Network;
import server.manager.*;
import server.map.Maps;
import server.manager.NetworkManager;
import server.network.model.Player;
import server.systems.RandomMovementSystem;
import shared.interfaces.Hero;
import shared.map.AutoTiler;
import shared.map.model.MapDescriptor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.artemis.E.E;

public class Server  {

    private final int tcpPort;
    private final int udpPort;
    private World world;
    private final WorldConfigurationBuilder builder = new WorldConfigurationBuilder();
    private Map<Class<? extends IManager>, IManager> managers = new HashMap<>();
    private KryonetServerMarshalStrategy strategy;

    public Server(int tcpPort, int udpPort) {
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
    }

    public void create() {
        initWorld();
        createMap();
        initManagers();
    }

    private void initManagers() {
        managers.put(NetworkManager.class, new NetworkManager(this, strategy));
        managers.put(CombatManager.class, new CombatManager(this));
        managers.put(ItemManager.class, new ItemManager(this));
        managers.put(SpellManager.class, new SpellManager(this));
        managers.put(MapManager.class, new MapManager(this));
        managers.put(WorldManager.class, new WorldManager(this));
        managers.put(SpellManager.class, new SpellManager(this));
    }

    public World getWorld() {
        return world;
    }

    private void initWorld() {
        System.out.println("Initializing systems...");

        strategy = new KryonetServerMarshalStrategy(tcpPort, udpPort);
        builder
                .with(new SuperMapper())
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
        // TODO
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
