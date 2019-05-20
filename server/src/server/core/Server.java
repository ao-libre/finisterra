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
import server.systems.manager.*;
import shared.model.lobby.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Server {

    private final int tcpPort;
    private final int udpPort;
    private int roomId;
    private ObjectManager objectManager;
    private SpellManager spellManager;
    private World world;
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
        createWorld();
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
                .with(new MapManager(this))
                .with(spellManager)
                .with(objectManager)
                .with(new NPCManager())
                .with(new PathFindingSystem(0.4f))
                .with(new WorldManager(this))
                .with(new PhysicalCombatSystem(this))
                .with(new MagicCombatSystem(this))
                .with(new EnergyRegenerationSystem(1f))
                .with(new MeditateSystem(this, 0.4f))
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
