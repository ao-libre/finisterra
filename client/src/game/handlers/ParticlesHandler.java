package game.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.utils.LongMap;
import game.AOGame;
import net.mostlyoriginal.api.system.core.PassiveSystem;

public class ParticlesHandler extends PassiveSystem {
    private static LongMap<ParticleEffectPool> particles = new LongMap<>();
    private AOAssetManager assetManager;

    public static ParticleEffect getParticle(int index) {
        return particles.get(index).obtain();
    }

    @Override
    protected void initialize() {
        super.initialize();
        AOGame game = (AOGame) Gdx.app.getApplicationListener();
        assetManager = game.getAssetManager();
        particles.put(1, new ParticleEffectPool(assetManager.getParticle("blue-meditation.p"), 1, 100));
        particles.put(2, new ParticleEffectPool(assetManager.getParticle("aura1.party"), 1, 100));
        particles.put(3, new ParticleEffectPool(assetManager.getParticle("healing-2.p"), 20, 100));
        particles.put(4, new ParticleEffectPool(assetManager.getParticle("healing-2.p"), 20, 100));
    }
}
