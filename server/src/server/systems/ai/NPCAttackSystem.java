package server.systems.ai;

import com.artemis.Aspect;
import com.artemis.E;
import entity.character.states.Heading;
import entity.character.status.Hit;
import entity.npc.NPC;
import position.WorldPos;
import server.systems.IntervalFluidIteratingSystem;
import server.systems.combat.CombatSystem;
import server.systems.combat.PhysicalCombatSystem;
import server.systems.manager.MapManager;
import server.systems.manager.WorldManager;
import server.utils.WorldUtils;
import shared.network.notifications.EntityUpdate.EntityUpdateBuilder;

import java.util.Optional;

import static com.artemis.E.E;
import static server.utils.WorldUtils.WorldUtils;

public class NPCAttackSystem extends IntervalFluidIteratingSystem {

    // should interval be per npc?
    public NPCAttackSystem(float interval) {
        super(Aspect.all(NPC.class, Hit.class, WorldPos.class, Heading.class), interval);
    }

    @Override
    protected void process(E e) {
        CombatSystem system = world.getSystem(PhysicalCombatSystem.class);
        world
                .getSystem(MapManager.class)
                .getNearEntities(e.id())
                .stream()
                .filter(e2 -> E(e2) != null)
                .filter(e2 -> !E(e2).hasNPC())
                .filter(e2 -> E(e2).hasWorldPos())
                .filter(e2 -> inRange(e.id(), e2))
                .findFirst()
                .ifPresent(target -> system.entityAttack(e.id(), Optional.of(target)));

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
                world.getSystem(WorldManager.class).notifyUpdate(e, EntityUpdateBuilder.of(e).withComponents(npc.getHeading()).build());
                facingPos = targetPos;
            }
        }
        return facingPos.equals(targetPos);
    }
}
