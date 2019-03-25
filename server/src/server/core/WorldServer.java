package server.core;

import server.database.IDatabase;
import server.manager.MapManager;
import server.manager.ObjectManager;
import server.manager.SpellManager;
import server.manager.WorldManager;
import server.network.NetworkComunicator;
import server.systems.RandomMovementSystem;
import shared.interfaces.Hero;
import com.artemis.Entity;
import com.artemis.SuperMapper;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;

import static com.artemis.E.E;

public class WorldServer {

    public static final int INVALID_USER = -1;
    private boolean running = true;
    private boolean pause = false;
    private static World world;
    private static IDatabase database;

    private final WorldConfigurationBuilder builder = new WorldConfigurationBuilder();
    private NetworkComunicator networkComunicator;

    public static World getWorld() {
        return world;
    }

    void initSystems() {
        System.out.println("Initializing systems...");
        MapManager.initialize();
        ObjectManager.load();
        SpellManager.load();
        KryonetServerMarshalStrategy server = new KryonetServerMarshalStrategy();
        networkComunicator = new NetworkComunicator(server);
        builder
                .with(new SuperMapper())
                .with(new ServerSystem(server))
                .with(new RandomMovementSystem());
        // Logic systems
        // TODO AI-NPC
    }


    void createWorld() {
        System.out.println("Creating world...");
        world = new World(builder.build());
        // testing
        Entity player2 = WorldManager.createEntity("guidota2", Hero.WARRIOR.ordinal());
        E(player2).randomMovement();
        MapManager.updateEntity(player2.getId());
    }

    void start() {
        new Thread(() -> {
            double ns = 1000000000.0 / 60.0;
            double delta = 0;

            long lastTime = System.nanoTime();
            long timer = System.currentTimeMillis();

            while (running) {
                if (pause) {
                    continue;
                }
                long now = System.nanoTime();
                delta += (now - lastTime) / ns;
                lastTime = now;

                while (delta >= 1) {
                    world.process();
                    delta--;
                }
            }
            System.out.println("Server down");
            networkComunicator.stop();
        }).start();
    }


    public void pause() {
        this.pause = true;
    }

    public void resume() {
        this.pause = false;
    }

    public void stop() {
        running = false;
    }
}
