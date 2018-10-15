package graphics;

import com.artemis.Component;
import com.artemis.annotations.DelayedComponentRemoval;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

@DelayedComponentRemoval
public class FX extends Component {

    public Deque<Integer> fxs = new ConcurrentLinkedDeque<>();
    public Deque<Integer> particles = new ConcurrentLinkedDeque<>();

    public FX() {
    }

    public void addFx(Integer fx) {
        fxs.add(fx);
    }

    public void removeFx(int fx) {
        fxs.remove(fx);
    }

    public void addParticleEffect(Integer particleEffect) {
        particles.add(particleEffect);
    }

    public void removeParticle(Integer particleEffect) {
        particles.remove(particleEffect);
    }


}
