package ar.com.tamborindeguy.systems;

import com.artemis.Aspect;
import com.artemis.systems.IteratingSystem;
import movement.RandomMovement;
import physics.AOPhysics;

import java.util.*;

import static com.artemis.E.E;

public class RandomMovementSystem extends IteratingSystem {
    private static final List<AOPhysics.Movement> VALUES =
            Collections.unmodifiableList(Arrays.asList(AOPhysics.Movement.values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    public static Optional<AOPhysics.Movement> randomMovement()  {
        int index = RANDOM.nextInt(SIZE * 100);
        return Optional.ofNullable(VALUES.size() > index ? VALUES.get(index) : null);
    }

    public RandomMovementSystem() {
        super(Aspect.all(RandomMovement.class, AOPhysics.class));
    }

    @Override
    protected void process(int entityId) {
        AOPhysics aoPhysics = E(entityId).getAOPhysics();
        Optional<AOPhysics.Movement> randomMovement = randomMovement();
        randomMovement.ifPresent(movement -> aoPhysics.addIntention(movement));
        otherMovements(randomMovement).forEach(movement -> aoPhysics.removeIntention(movement));
    }

    private List<AOPhysics.Movement> otherMovements(Optional<AOPhysics.Movement> movement) {
        return movement.isPresent() ? getOther(movement.get()) : VALUES;
    }

    private List<AOPhysics.Movement> getOther(AOPhysics.Movement movement) {
        List<AOPhysics.Movement> result = new ArrayList<>();
        result.addAll(VALUES);
        result.remove(movement);
        return result;
    }
}
