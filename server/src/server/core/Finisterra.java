package server.core;

import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.ApplicationListener;
import server.network.ServerNotificationProcessor;
import server.network.model.Lobby;
import server.network.model.Room;

import java.util.HashSet;
import java.util.Set;

public class Finisterra implements ApplicationListener {

    private Set<Server> servers = new HashSet<>();
    private final int tcpPort;
    private final int udpPort;
    private int lastPort;
    private Lobby lobby;

    public Finisterra(int tcpPort, int udpPort) {
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
        this.lastPort = udpPort;
    }

    @Override
    public void create() {
        // TODO crear la conexion donde va a recibir requests de los usuarios
        lobby = new Lobby();
        WorldConfigurationBuilder worldConfigurationBuilder = new WorldConfigurationBuilder();
        KryonetServerMarshalStrategy strategy = new KryonetServerMarshalStrategy(tcpPort, udpPort);

        worldConfigurationBuilder
                .with(new ServerSystem(null, strategy)); // TODO
    }

    public void startGame(Room room) {
        // TODO create server
        Server server = new Server(getNextPort(), getNextPort());
        // TODO add players
        server.addPlayers(room.getPlayers());
        // TODO initialize
    }

    private int getNextPort() {
        return ++lastPort;
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void render() {
        servers.forEach(Server::update);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {}
}
