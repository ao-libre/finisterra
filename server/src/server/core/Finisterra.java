package server.core;

import com.artemis.FluidEntityPlugin;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.esotericsoftware.minlog.Log;
import server.systems.account.UserSystem;
import server.systems.ai.RandomMovementSystem;
import server.systems.config.ConfigurationSystem;
import server.systems.config.NPCSystem;
import server.systems.config.ObjectSystem;
import server.systems.config.SpellSystem;
import server.systems.entity.factory.ComponentSystem;
import server.systems.entity.factory.EntityFactorySystem;
import server.systems.entity.user.*;
import server.systems.item.ItemActionSystem;
import server.systems.item.ItemSystem;
import server.systems.item.ItemUsageSystem;
import server.systems.network.*;
import server.systems.account.AccountSystem;
import server.systems.ai.NPCAttackSystem;
import server.systems.ai.PathFindingSystem;
import server.systems.ai.RespawnSystem;
import server.systems.combat.MagicCombatSystem;
import server.systems.combat.PhysicalCombatSystem;
import server.systems.combat.RangedCombatSystem;
import server.systems.entity.training.CharacterTrainingSystem;
import server.systems.world.*;
import server.utils.EntityJsonSerializer;
import shared.systems.IntervalSystem;
import shared.util.LogSystem;
import shared.util.MapHelper;

import java.util.concurrent.TimeUnit;

import static server.utils.Intervals.*;
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

        builder
                .with(new ClearSystem())
                .with(new ConfigurationSystem())
                .with(new ServerSystem())
                .with(new EntityJsonSerializer())
                .with(new UserSystem())
                .with(new AccountSystem())
                .with(new ServerNotificationProcessor())
                .with(new FluidEntityPlugin())
                .with(new ComponentSystem())
                .with(new EntityFactorySystem())
                .with(new IntervalSystem())
                .with(new ServerReferenceSystem())
                .with(new ItemSystem())
                .with(new ServerRequestProcessor())
                .with(new ItemUsageSystem())
                .with(new NPCSystem())
                .with(new MapSystem())
                .with(new SpellSystem())
                .with(new ObjectSystem())
                .with(new WorldEntitiesSystem())
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
                .with(new WorldSaveSystem(5*60*1000)); // 5 minutes
        world = new World(builder.build());

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
