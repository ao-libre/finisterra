package game.handlers;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.utils.LongMap;
import game.AOGame;
import net.mostlyoriginal.api.system.core.PassiveSystem;

public class ParticlesHandler extends PassiveSystem {
    private static final LongMap<ParticleEffectPool> PARTICLES = new LongMap<>();
    private AOAssetManager assetManager;

    public static ParticleEffect getParticle(int index) {
        return PARTICLES.get(index).obtain();
    }

    @Override
    protected void initialize() {
        super.initialize();
        assetManager = AOGame.getGlobalAssetManager();
        PARTICLES.put(1, new ParticleEffectPool(assetManager.getParticle("blue-meditation.p"), 1, 100));
        PARTICLES.put(2, new ParticleEffectPool(assetManager.getParticle("aura1.party"), 1, 100));
    }
}
