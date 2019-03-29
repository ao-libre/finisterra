package server.core;

import com.artemis.Entity;
import com.artemis.SuperMapper;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import server.manager.MapManager;
import server.manager.ObjectManager;
import server.manager.SpellManager;
import server.manager.WorldManager;
import server.map.Maps;
import server.network.NetworkComunicator;
import server.systems.RandomMovementSystem;
import shared.interfaces.Hero;
import shared.map.AutoTiler;
import shared.map.model.MapDescriptor;

import static com.artemis.E.E;

public class Server implements ApplicationListener {

    private static World world;
    private final WorldConfigurationBuilder builder = new WorldConfigurationBuilder();

    @Override
    public void create() {
        initSystems();
        createWorld();
        createMap();
    }

    public static World getWorld() {
        return world;
    }

    private void initSystems() {
        System.out.println("Initializing systems...");

        ObjectManager.load();
        SpellManager.load();

        KryonetServerMarshalStrategy server = new KryonetServerMarshalStrategy();

        builder
                .with(new SuperMapper())
                .with(new ServerSystem(server))
                .with(new RandomMovementSystem());
        new NetworkComunicator(server);
    }


    private void createWorld() {
        System.out.println("Creating world...");
        world = new World(builder.build());
        // testing
        Entity player2 = WorldManager.createEntity("guidota2", Hero.WARRIOR.ordinal());
        E(player2).randomMovement();
        MapManager.updateEntity(player2.getId());
    }

    private void createMap() {
        String path = "map/tileset.json";
        MapDescriptor map = AutoTiler.load(50, 50, Gdx.files.internal(path));
        Maps.generateMapEntity(map, path);
    }


    @Override
    public void resize(int width, int height) {}

    @Override
    public void render() {
        world.process();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {}
}
