package graphics;

import com.artemis.Component;
import com.artemis.annotations.DelayedComponentRemoval;
import com.artemis.annotations.PooledWeaver;
import entity.Index;

import java.io.Serializable;

@PooledWeaver
@DelayedComponentRemoval
public class Effect extends Component implements Serializable, Index {

    public static final int NO_REF = -1;

    public int entityReference = NO_REF;
    public int effectId;
    public int loops = -1;
    public Type type;
    public boolean renderBefore;
    public boolean renderAfter;

    public Effect() {
    }

    @Override
    public int getIndex() {
        return effectId;
    }

    public enum Type {
        PARTICLE,
        FX
    }

    public static class EffectBuilder {
        Effect effect;

        public EffectBuilder() {
            this.effect = new Effect();
        }

        public EffectBuilder attachTo(int entityReference) {
            effect.entityReference = entityReference;
            return this;
        }

        public EffectBuilder withFX(int fx) {
            effect.type = Type.FX;
            effect.effectId = fx;
            return this;
        }

        public EffectBuilder withLoops(int loops) {
            effect.loops = loops;
            return this;
        }

        public EffectBuilder withParticle(int particle) {
            effect.type = Type.PARTICLE;
            effect.effectId = particle;
            return this;
        }

        public Effect build() {
            return effect;
        }
    }
}
