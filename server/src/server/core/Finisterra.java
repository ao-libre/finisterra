package server.core;

import com.artemis.FluidEntityPlugin;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import server.systems.FinisterraSystem;
import server.systems.manager.LobbyNetworkManager;
import server.systems.manager.ObjectManager;
import server.systems.manager.SpellManager;
import server.utils.IpChecker;
import shared.model.lobby.Lobby;
import shared.model.lobby.Room;
import shared.model.map.Map;
import shared.network.lobby.StartGameResponse;
import shared.util.MapHelper;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Finisterra implements ApplicationListener {

    private final int tcpPort;
    private final int udpPort;
    private Set<Server> servers = new HashSet<>();
    private int lastPort;
    private Lobby lobby;
    private World world;
    private LobbyNetworkManager networkManager;
    private ObjectManager objectManager;
    private SpellManager spellManager;
    private HashMap<Integer, Map> maps = new HashMap<>();

    public Finisterra(int tcpPort, int udpPort) {
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
        this.lastPort = udpPort;
    }

    @Override
    public void create() {
        long start = System.currentTimeMillis();
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        init();
        Gdx.app.log("Server initialization", "Finisterra...");
        MapHelper.instance();
        lobby = new Lobby();
        WorldConfigurationBuilder worldConfigurationBuilder = new WorldConfigurationBuilder();
        KryonetServerMarshalStrategy strategy = new KryonetServerMarshalStrategy(tcpPort, udpPort);
        networkManager = new LobbyNetworkManager(strategy);
        world = new World(worldConfigurationBuilder
                .with(new FluidEntityPlugin())
                .with(new FinisterraSystem(this, strategy))
                .build());
        Gdx.app.log("Server initialization", "Elapsed time: " + (start - System.currentTimeMillis()));
        Gdx.app.log("Server initialization", "Finisterra OK");

    }

    private void init() {
        objectManager = new ObjectManager();
        spellManager = new SpellManager();
    }

    public void startGame(Room room) {
        Server roomServer = servers.stream().filter(server -> server.getRoomId() == room.getId()).findFirst().orElseGet(() -> {
            int tcpPort = getNextPort();
            int udpPort = getNextPort();
            Server server = new Server(room.getId(), tcpPort, udpPort, objectManager, spellManager, maps);
            server.addPlayers(room.getPlayers());
            servers.add(server);
            return server;
        });
        room.getPlayers().forEach(player -> {
            int connectionId = getNetworkManager().getConnectionByPlayer(player);
            try {
                final String ip = IpChecker.getIp();
                String property = System.getProperty("server.useLocalhost");
                System.out.println(property);
                boolean shouldUseLocalHost = Boolean.parseBoolean(property);
                InetAddress inetAddress = InetAddress.getLocalHost();

                getNetworkManager().sendTo(connectionId, new StartGameResponse(shouldUseLocalHost ? inetAddress.getHostAddress() : ip, roomServer.getTcpPort(), roomServer.getUdpPort()));
            } catch (UnknownHostException e) {
                e.printStackTrace();
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

    public LobbyNetworkManager getNetworkManager() {
        return networkManager;
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void render() {
        world.process();
        servers.forEach(Server::update);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}
