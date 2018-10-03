package ar.com.tamborindeguy.client.systems;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.Entity;
import com.artemis.EntitySubscription;
import com.artemis.utils.IntBag;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


/**
 * Tracks a subset of entities, but does not implement any sorting or iteration.
 * <p>
 * Like {@link BaseEntitySystem}, but uses Entity references instead of int.
 * <p>
 * This system exists as a convenience for users migrating from other Artemis
 * clones or older versions of odb. We recommend using the int systems over
 * the Entity variants.
 *
 * @author Arni Arent
 * @author Adrian Papari
 */
public abstract class OrderedEntityProcessingSystem extends BaseEntitySystem
        implements EntitySubscription.SubscriptionListener {

    private boolean shouldSyncEntities;
    private ArrayList<Entity> entities = new ArrayList<Entity>();

    public OrderedEntityProcessingSystem(Aspect.Builder aspect) {
        super(aspect);
    }

    @Override
    public final void inserted(IntBag entities) {
        shouldSyncEntities = true;
    }

    @Override
    public final void removed(IntBag entities) {
        shouldSyncEntities = true;
    }

    private List<Entity> getEntities() {
        if (shouldSyncEntities) {
            IntBag intBag = subscription.getEntities();
            int[] ids = intBag.getData();
            entities.clear();
            for (int i = 0; i < intBag.size(); i++) {
                this.entities.add(world.getEntity(ids[i]));
            }
            shouldSyncEntities = false;
        }
        entities.sort(getComparator());
        return entities;
    }

    @Override
    protected final void processSystem() {
        getEntities().forEach(e -> process(e));
    }

    protected abstract void process(Entity e);

    protected abstract Comparator<? super Entity> getComparator();

}
