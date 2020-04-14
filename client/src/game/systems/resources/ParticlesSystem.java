package game.systems.resources;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.utils.LongMap;
import game.handlers.DefaultAOAssetManager;
import net.mostlyoriginal.api.system.core.PassiveSystem;

@Wire
public class ParticlesSystem extends PassiveSystem {
    private static final LongMap<ParticleEffectPool> PARTICLES = new LongMap<>();
    @Wire
    private DefaultAOAssetManager assetManager;
    public static ParticleEffect getParticle(int index) {
        return PARTICLES.get(index).obtain();
    }

    @Override
    protected void initialize() {
        super.initialize();
        PARTICLES.put(1, new ParticleEffectPool(assetManager.getParticle("meditate1.party"), 1, 100));
        PARTICLES.put(2, new ParticleEffectPool(assetManager.getParticle("aura1.party"), 1, 100));
    }
}
