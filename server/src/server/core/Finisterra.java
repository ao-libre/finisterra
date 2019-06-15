package server.core;

import com.artemis.FluidEntityPlugin;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import server.network.FinisterraRequestProcessor;
import server.systems.FinisterraSystem;
import server.systems.manager.ObjectManager;
import server.systems.manager.SpellManager;
import server.utils.IpChecker;
import shared.model.lobby.Lobby;
import shared.model.lobby.Room;
import shared.model.map.Map;
import shared.network.lobby.StartGameResponse;
import shared.util.MapHelper;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static shared.util.MapHelper.CacheStrategy.NEVER_EXPIRE;

public class Finisterra implements ApplicationListener {

    private final int tcpPort;
    private final int udpPort;
    private Set<Server> servers = new HashSet<>();
    private int lastPort;
    private Lobby lobby;
    private World world;
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
        new Thread(() -> {
            MapHelper helper = MapHelper.instance(NEVER_EXPIRE);
            helper.loadAll();
        }).start();
        lobby = new Lobby();
        WorldConfigurationBuilder worldConfigurationBuilder = new WorldConfigurationBuilder();
        ServerStrategy strategy = new ServerStrategy(tcpPort, udpPort);
        world = new World(worldConfigurationBuilder
                .with(new FluidEntityPlugin())
                .with(new FinisterraSystem(strategy))
                .with(new FinisterraRequestProcessor())
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
        room.getPlayers().stream().mapToInt(player -> getNetworkManager().getConnectionByPlayer(player)).forEach(connectionId -> {
            try {
                final String ip = IpChecker.getIp();
                String property = System.getProperty("server.useLocalhost");
                System.out.println(property);
                boolean shouldUseLocalHost = Boolean.parseBoolean(property);
                InetAddress inetAddress = InetAddress.getLocalHost();

                getNetworkManager().sendTo(connectionId, new StartGameResponse(shouldUseLocalHost ? inetAddress.getHostAddress() : ip, roomServer.getTcpPort(), roomServer.getUdpPort()));
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
