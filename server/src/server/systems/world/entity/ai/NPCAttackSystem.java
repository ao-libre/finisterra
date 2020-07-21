package server.systems.world.entity.ai;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import component.entity.character.states.Heading;
import component.entity.character.status.Hit;
import component.entity.npc.NPC;
import component.position.WorldPos;
import server.systems.network.EntityUpdateSystem;
import server.systems.world.IntervalFluidIteratingSystem;
import server.systems.world.MapSystem;
import server.systems.world.entity.combat.PhysicalCombatSystem;
import server.utils.UpdateTo;
import server.utils.WorldUtils;
import shared.network.notifications.EntityUpdate;
import shared.util.EntityUpdateBuilder;

import java.util.Optional;

import static com.artemis.E.E;
import static server.utils.WorldUtils.WorldUtils;

@Wire
public class NPCAttackSystem extends IntervalFluidIteratingSystem {

    private MapSystem mapSystem;
    private PhysicalCombatSystem combatSystem;
    private EntityUpdateSystem entityUpdateSystem;

    // should interval be per npc?
    public NPCAttackSystem(float interval) {
        super(Aspect.all(NPC.class, Hit.class, WorldPos.class, Heading.class), interval);
    }

    @Override
    protected void process(E e) {
        mapSystem
                .getNearEntities(e.id())
                .stream()
                .filter(e2 -> E(e2) != null)
                .filter(e2 -> !E(e2).hasNPC())
                .filter(e2 -> E(e2).hasWorldPos())
                .filter(e2 -> E(e2).healthMin() != 0)
                .filter(e2 -> inRange(e.id(), e2))
                .findFirst()
                .ifPresent(target -> combatSystem.entityAttack(e.id(), Optional.of(target)));

    }

    private boolean inRange(int e, int e2) {
        E npc = E(e);
        E target = E(e2);
        WorldUtils worldUtils = WorldUtils(world);
        WorldPos targetPos = target.getWorldPos();
        WorldPos npcPos = npc.getWorldPos();
        WorldPos facingPos = worldUtils.getFacingPos(npcPos, npc.getHeading());
        if (worldUtils.distance(npcPos, targetPos) == 1) {
            if (!npc.isImmobile() && !facingPos.equals(targetPos)) {
                // move heading
                npc.headingCurrent(worldUtils.getHeading(npcPos, targetPos));
                EntityUpdate update = EntityUpdateBuilder.of(e).withComponents(npc.getHeading()).build();
                entityUpdateSystem.add(update, UpdateTo.ALL);
                facingPos = targetPos;
            }
        }
        return facingPos.equals(targetPos);
    }
}
