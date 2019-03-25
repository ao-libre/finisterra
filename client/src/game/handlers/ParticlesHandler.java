package game.handlers;

import game.AOGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.utils.LongMap;

public class ParticlesHandler {
    private static LongMap<ParticleEffectPool> particles = new LongMap<>();

    public static void load() {
        ParticleEffect effect = new ParticleEffect();
        effect.load(Gdx.files.internal(AOGame.GAME_FXS_PATH + "meditate1.party"), Gdx.files.internal(""));
        particles.put(1, new ParticleEffectPool(effect, 1, 100));
        ParticleEffect aura = new ParticleEffect();
        aura.load(Gdx.files.internal(AOGame.GAME_FXS_PATH + "aura1.party"), Gdx.files.internal(AOGame.GAME_PARTICLES_PATH));
        particles.put(2, new ParticleEffectPool(aura, 1, 100));
    }

    public static ParticleEffect getParticle(int index) {
        return particles.get(index).obtain();
    }
}
