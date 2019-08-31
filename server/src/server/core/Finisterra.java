package server.core;

import com.artemis.FluidEntityPlugin;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import server.ServerConfiguration;
import server.network.FinisterraRequestProcessor;
import server.systems.FinisterraSystem;
import server.systems.manager.ObjectManager;
import server.systems.manager.SpellManager;
import server.utils.IpChecker;
import shared.model.lobby.Lobby;
import shared.model.lobby.Room;
import shared.network.lobby.StartGameResponse;
import shared.util.MapHelper;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static shared.util.MapHelper.CacheStrategy.NEVER_EXPIRE;

public class Finisterra implements ApplicationListener {

    private final int tcpPort;
    private final int udpPort;
    private boolean shouldUseLocalHost;
    private HashMap<Integer, Server> servers = new HashMap<>();
    private int lastPort;
    private Lobby lobby;
    private World world;
    private ObjectManager objectManager;
    private SpellManager spellManager;

    public Finisterra(ServerConfiguration config) {

        /*
          Fetch ports configuration from Server.json
         */
        ServerConfiguration.Network.Ports currentPorts = config.getNetwork().getPorts();

        this.tcpPort = currentPorts.getTcpPort();
        this.udpPort = currentPorts.getUdpPort();
        this.lastPort = currentPorts.getUdpPort();

        this.shouldUseLocalHost = config.getNetwork().getuseLocalHost();
    }

    @Override
    public void create() {
        long start = System.currentTimeMillis();
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        Gdx.app.log("Server initialization", "Initializing Finisterra Server...");

        objectManager = new ObjectManager();
        spellManager = new SpellManager();

        Thread thread = new Thread(() -> {
            MapHelper helper = MapHelper.instance(NEVER_EXPIRE);
            helper.loadAll();
        });
        thread.setDaemon(true);
        thread.start();

        lobby = new Lobby();
        WorldConfigurationBuilder worldConfigurationBuilder = new WorldConfigurationBuilder();
        ServerStrategy strategy = new ServerStrategy(tcpPort, udpPort);

        world = new World(worldConfigurationBuilder
                .with(new FluidEntityPlugin())
                .with(new FinisterraSystem(strategy))
                .with(new FinisterraRequestProcessor())
                .build());

        Gdx.app.log("Server initialization", "Elapsed time: " + TimeUnit.MILLISECONDS.toSeconds(Math.abs(start - System.currentTimeMillis())) + " seconds.");
        Gdx.app.log("Server initialization", "Finisterra OK");
    }

    public void startGame(Room room) {
        Server roomServer = servers.computeIfAbsent(room.getId(), (id) -> {
            int tcpPort = getNextPort();
            int udpPort = getNextPort();
            return new Server(id, tcpPort, udpPort, objectManager, spellManager);
        });
        room.getPlayers().stream().mapToInt(player -> getNetworkManager().getConnectionByPlayer(player)).forEach(connectionId -> {
            try {
                if (shouldUseLocalHost) {
                    System.out.println("Using localhost...");
                }
                getNetworkManager().sendTo(connectionId, new StartGameResponse(
                        shouldUseLocalHost ? InetAddress.getLocalHost().getHostAddress() : IpChecker.getIp(),
                        roomServer.getTcpPort(), roomServer.getUdpPort()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private int getNextPort() {
        return ++lastPort;
    }

    public Lobby getLobby() {
        return lobby;
    }

    private FinisterraSystem getNetworkManager() {
        return world.getSystem(FinisterraSystem.class);
    }

    public Optional<Server> getServer(int roomId) {
        return Optional.ofNullable(servers.get(roomId));
    }

    public Optional<Room> getRoom(Server server) {
        return getLobby().getRoom(server.getRoomId());
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void render() {
        world.process();
        servers.values().forEach(Server::update);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        getLobby().getWaitingPlayers().clear();
        getLobby().getRooms().clear();
        getNetworkManager().stop();
        System.exit(0);
    }
}
