package graphics;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class FX extends Component {

    public Deque<Integer> fxs = new ConcurrentLinkedDeque<>();
    public Deque<Integer> particles = new ConcurrentLinkedDeque<>();

    public FX() {
    }

    public void addFx(Integer fx) {
        fxs.add(fx);
    }

    public void addParticleEffect(Integer particleEffect) {
        particles.add(particleEffect);
    }

    public void remove(Integer particleEffect) {
        particles.remove(particleEffect);
    }

    public void remove(int fx) {
        fxs.remove(fx);
    }


}
