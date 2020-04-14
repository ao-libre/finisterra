package server.core;

import com.artemis.FluidEntityPlugin;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.io.JsonArtemisSerializer;
import com.artemis.managers.TagManager;
import com.artemis.managers.WorldSerializationManager;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.esotericsoftware.minlog.Log;
import server.configs.ServerConfiguration;
import server.manager.ConfigurationManager;
import server.network.ServerNotificationProcessor;
import server.network.ServerRequestProcessor;
import server.systems.*;
import server.systems.account.AccountSystem;
import server.systems.ai.NPCAttackSystem;
import server.systems.ai.PathFindingSystem;
import server.systems.ai.RespawnSystem;
import server.systems.combat.MagicCombatSystem;
import server.systems.combat.PhysicalCombatSystem;
import server.systems.combat.RangedCombatSystem;
import server.systems.entity.EffectEntitySystem;
import server.systems.entity.MovementSystem;
import server.systems.entity.SoundEntitySystem;
import server.systems.manager.*;
import server.systems.network.EntityUpdateSystem;
import server.systems.network.MessageSystem;
import server.systems.network.ServerReferenceSystem;
import server.systems.user.ItemActionSystem;
import server.systems.user.PlayerActionSystem;
import server.systems.user.UserSystem;
import shared.systems.IntervalSystem;
import shared.util.LogSystem;
import shared.util.MapHelper;

import java.util.concurrent.TimeUnit;

import static server.systems.Intervals.*;
import static shared.util.MapHelper.CacheStrategy.NEVER_EXPIRE;

public class Finisterra extends ApplicationAdapter {

    private World world;
    private float currentTick = 0;

    @Override
    public void create() {
        long start = System.currentTimeMillis();

        Log.setLogger(new LogSystem());
        Log.info("Server Initialization", "Initializing Finisterra Server...");

        loadAsync();
        createWorld();

        Log.info("Server initialization", "Elapsed time: " + TimeUnit.MILLISECONDS.toSeconds(Math.abs(start - System.currentTimeMillis())) + " seconds.");
        Log.info("Server initialization", "Finisterra OK");
    }

    private void loadAsync() {
        Thread thread = new Thread(() -> {
            MapHelper helper = MapHelper.instance(NEVER_EXPIRE);
            helper.loadAll();
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void createWorld() {
        Log.info("Initializing systems...");
        final WorldConfigurationBuilder builder = new WorldConfigurationBuilder();

        ServerConfiguration serverConfig = ConfigurationManager.getInstance().getServerConfig();
        ServerConfiguration.Network.Ports currentPorts = serverConfig.getNetwork().getPorts();

        WorldSerializationManager serializationManager = new WorldSerializationManager();

        builder
                .with(new ClearSystem())
                .with(new ServerSystem(new ServerStrategy(currentPorts.getTcpPort(), currentPorts.getUdpPort())))
                .with(new UserSystem())
                .with(new AccountSystem())
                .with(new ServerNotificationProcessor())
                .with(new FluidEntityPlugin())
                .with(new ComponentManager())
                .with(new EntityFactorySystem())
                .with(new IntervalSystem())
                .with(new ServerReferenceSystem())
                .with(new ItemManager())
                .with(new ServerRequestProcessor())
                .with(new ItemConsumers())
                .with(new NPCManager())
                .with(new MapManager())
                .with(new SpellManager())
                .with(new ObjectManager())
                .with(new WorldManager())
                .with(new PhysicalCombatSystem())
                .with(new RangedCombatSystem())
                .with(new CharacterTrainingSystem())
                .with(new MagicCombatSystem())
                .with(new PathFindingSystem(PATH_FINDING_INTERVAL))
                .with(new NPCAttackSystem(NPC_ATTACK_INTERVAL))
                .with(new EnergyRegenerationSystem(ENERGY_REGENERATION_INTERVAL))
                .with(new MeditateSystem(MEDITATE_INTERVAL))
                .with(new FootprintSystem(FOOTPRINT_LIVE_TIME))
                .with(new EffectEntitySystem())
                .with(new SoundEntitySystem())
                .with(new RandomMovementSystem())
                .with(new RespawnSystem())
                .with(new BuffSystem())
                .with(new CommandSystem())
                .with(new EntityUpdateSystem())
                .with(new MessageSystem())
                .with(new TagManager())
                .with(new MovementSystem())
                .with(new PlayerActionSystem())
                .with(new ItemActionSystem())
                .with(serializationManager);
        world = new World(builder.build());
        serializationManager.setSerializer(new JsonArtemisSerializer(world));

        Log.info("World created successfully!");
    }

    @Override
    public void render() {
        world.setDelta(Gdx.graphics.getDeltaTime());
        world.process();
    }

    @Override
    public void dispose() {
        System.exit(0);
    }
}
