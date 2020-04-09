package component.graphic;

public class EffectBuilder {
    Effect effect;

    private EffectBuilder() {
        this.effect = new Effect();
    }

    public static EffectBuilder create() {
        return new EffectBuilder();
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
