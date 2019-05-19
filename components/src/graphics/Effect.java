package graphics;

import com.artemis.Component;

import java.io.Serializable;

public class Effect extends Component implements Serializable {

    public static final int NO_REF = -1;

    public int entityReference = NO_REF;
    public int effectId;
    public Type type;

    public Effect() {}

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

        public EffectBuilder withParticle(int particle) {
            effect.type = Type.PARTICLE;
            effect.effectId = particle;
            return this;
        }

        public Effect build() {
            return effect;
        }
    }

    public enum Type {
        PARTICLE,
        FX
    }
}
