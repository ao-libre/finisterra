package server.core;

import com.artemis.FluidEntityPlugin;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.esotericsoftware.minlog.Log;
import server.systems.account.AccountSystem;
import server.systems.account.UserSystem;
import server.systems.config.ConfigurationSystem;
import server.systems.config.NPCSystem;
import server.systems.config.ObjectSystem;
import server.systems.config.SpellSystem;
import server.systems.network.*;
import server.systems.world.MapSystem;
import server.systems.world.WorldEntitiesSystem;
import server.systems.world.WorldSaveSystem;
import server.systems.world.entity.ai.NPCAttackSystem;
import server.systems.world.entity.ai.PathFindingSystem;
import server.systems.world.entity.ai.RandomMovementSystem;
import server.systems.world.entity.ai.RespawnSystem;
import server.systems.world.entity.combat.MagicCombatSystem;
import server.systems.world.entity.combat.PhysicalCombatSystem;
import server.systems.world.entity.combat.RangedCombatSystem;
import server.systems.world.entity.factory.*;
import server.systems.world.entity.item.ItemActionSystem;
import server.systems.world.entity.item.ItemSystem;
import server.systems.world.entity.item.ItemUsageSystem;
import server.systems.world.entity.movement.FootprintSystem;
import server.systems.world.entity.movement.MovementSystem;
import server.systems.world.entity.npc.NPCActionSystem;
import server.systems.world.entity.training.CharacterTrainingSystem;
import server.systems.world.entity.user.*;
import server.utils.EntityJsonSerializer;
import shared.systems.IntervalSystem;
import shared.util.LogSystem;
import shared.util.MapHelper;

import java.util.concurrent.TimeUnit;

import static server.utils.Intervals.*;
import static shared.util.MapHelper.CacheStrategy.NEVER_EXPIRE;

public class Finisterra extends ApplicationAdapter {

    private World world;

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
        Thread mapHandler = new Thread(() -> {
            MapHelper helper = MapHelper.instance(NEVER_EXPIRE);
            helper.loadAll();
        });
        mapHandler.setDaemon(true);
        mapHandler.start();

        // Este thread chequea que existan las carpetas necesarias para que el servidor opere correctamente.
        Thread requiredDirectoriesAnalyzer = new Thread(() -> {
            AccountSystem.checkStorageDirectory();
            UserSystem.checkStorageDirectory();
        });
        requiredDirectoriesAnalyzer.start();
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
                .with(new NPCActionSystem())
                .with(new MapSystem())
                .with(new SpellSystem())
                .with(new ObjectSystem())
                .with(new ModifierSystem())
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
                .with(new WorldSaveSystem(5 * 60 * 1000)); // 5 minutes
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
