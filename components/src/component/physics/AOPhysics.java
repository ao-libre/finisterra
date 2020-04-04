package component.physics;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

import java.io.Serializable;
import java.util.Deque;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;

@PooledWeaver
public class AOPhysics extends Component implements Serializable {

    public final static float WALKING_VELOCITY = 165.0f;

    public Deque<Movement> intentions = new ConcurrentLinkedDeque<>();
    public float velocity = WALKING_VELOCITY;

    public AOPhysics() {
    }

    public float getVelocity() {
        return velocity * 2;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public Optional<Movement> getMovementIntention() {
        return Optional.ofNullable(intentions.isEmpty() ? null : intentions.getLast());
    }

    public void addIntention(Movement movement) {
        intentions.add(movement);
    }

    public void removeIntention(Movement movement) {
        intentions.remove(movement);
    }

    public enum Movement {
        UP,
        DOWN,
        RIGHT,
        LEFT;

        Movement() {
        }
    }

}
