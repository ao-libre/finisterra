package graphics;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class FX extends Component {

    public Deque<ParticleEffect> effects = new ConcurrentLinkedDeque<>();

    public FX() {}

    public void add(ParticleEffect effect) {
        effects.add(effect);
    }

    public void remove(ParticleEffect effect) {
        effects.remove(effect);
    }
}
