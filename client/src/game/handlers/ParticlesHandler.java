package game.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.utils.LongMap;
import game.AOGame;
import game.utils.Resources;
import net.mostlyoriginal.api.system.core.PassiveSystem;

public class ParticlesHandler extends PassiveSystem {
    private static LongMap<ParticleEffectPool> particles = new LongMap<>();
    private AOAssetManager assetManager;

    public static void load() {
        ParticleEffect effect = new ParticleEffect();
        effect.load(Gdx.files.internal(Resources.GAME_FXS_PATH + "meditate1.party"), Gdx.files.internal(""));
        particles.put(1, new ParticleEffectPool(effect, 1, 100));
        ParticleEffect aura = new ParticleEffect();
        aura.load(Gdx.files.internal(Resources.GAME_FXS_PATH + "aura1.party"), Gdx.files.internal(Resources.GAME_PARTICLES_PATH));
        particles.put(2, new ParticleEffectPool(aura, 1, 100));
    }

    public static ParticleEffect getParticle(int index) {
        return particles.get(index).obtain();
    }

    @Override
    protected void initialize() {
        super.initialize();
        AOGame game = (AOGame) Gdx.app.getApplicationListener();
        assetManager = game.getAssetManager();
        particles.put(1, new ParticleEffectPool(assetManager.getParticle("meditate1.party"), 1, 100));
        particles.put(2, new ParticleEffectPool(assetManager.getParticle("aura1.party"), 1, 100));
    }
}
