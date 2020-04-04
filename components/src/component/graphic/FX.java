package component.graphic;

import com.artemis.Component;
import com.artemis.annotations.DelayedComponentRemoval;
import com.artemis.annotations.PooledWeaver;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

@PooledWeaver
@DelayedComponentRemoval
public class FX extends Component {

    public Deque<Integer> fxs = new ConcurrentLinkedDeque<>();
    public Deque<Integer> particles = new ConcurrentLinkedDeque<>();

    public FX() {
    }

    public void addFx(Integer fx) {
        if (fx > 0) {
            fxs.add(fx);
        }
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
