package movement;

import com.artemis.Component;

import java.io.Serializable;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Movement extends Component implements Serializable {

    public Deque<Destination> destinations = new ConcurrentLinkedDeque<>();

    public Movement() {
    }

    public void add(Destination destination) {
        destinations.add(destination);
    }

    public Destination getCurrent() {
        return destinations.peekFirst();
    }

    public void completeCurrent() {
        destinations.removeFirst();
    }

    public boolean hasMovements() {
        return !destinations.isEmpty();
    }

}
