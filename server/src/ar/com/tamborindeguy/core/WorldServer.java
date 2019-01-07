package ar.com.tamborindeguy.core;

import ar.com.tamborindeguy.database.IDatabase;
import ar.com.tamborindeguy.interfaces.Hero;
import ar.com.tamborindeguy.manager.MapManager;
import ar.com.tamborindeguy.manager.ObjectManager;
import ar.com.tamborindeguy.manager.SpellManager;
import ar.com.tamborindeguy.manager.WorldManager;
import ar.com.tamborindeguy.network.NetworkComunicator;
import ar.com.tamborindeguy.network.inventory.InventoryUpdate;
import ar.com.tamborindeguy.objects.types.Obj;
import ar.com.tamborindeguy.objects.types.Type;
import ar.com.tamborindeguy.systems.RandomMovementSystem;
import com.artemis.Entity;
import com.artemis.SuperMapper;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import entity.Heading;
import entity.character.info.Inventory;

import java.util.Set;

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
        MapManager.addPlayer(player2.getId());
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
