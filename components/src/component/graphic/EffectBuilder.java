package design.graphic;

public class EffectBuilder {
    Effect effect;

    public EffectBuilder() {
        this.effect = new Effect();
    }

    public EffectBuilder attachTo(int entityReference) {
        effect.entityReference = entityReference;
        return this;
    }

    public EffectBuilder withFX(int fx) {
        effect.type = Effect.Type.FX;
        effect.effectId = fx;
        return this;
    }

    public EffectBuilder withLoops(int loops) {
        effect.loops = loops;
        return this;
    }

    public EffectBuilder withParticle(int particle) {
        effect.type = Effect.Type.PARTICLE;
        effect.effectId = particle;
        return this;
    }

    public Effect build() {
        return effect;
    }
}
