package server.systems.world.entity.ai;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IntervalIteratingSystem;
import component.entity.character.states.Heading;
import component.entity.character.states.Immobile;
import component.entity.character.status.Health;
import component.entity.character.status.Hit;
import component.entity.npc.NPC;
import component.position.WorldPos;
import server.systems.network.EntityUpdateSystem;
import server.systems.world.MapSystem;
import server.systems.world.entity.combat.PhysicalCombatSystem;
import server.utils.UpdateTo;
import server.utils.WorldUtils;
import shared.network.notifications.EntityUpdate;
import shared.util.EntityUpdateBuilder;

import java.util.Optional;

import static server.utils.WorldUtils.WorldUtils;

public class NPCAttackSystem extends IntervalIteratingSystem {

    private MapSystem mapSystem;
    private PhysicalCombatSystem combatSystem;
    private EntityUpdateSystem entityUpdateSystem;

    ComponentMapper<NPC> mNPC;
    ComponentMapper<WorldPos> mWorldPos;
    ComponentMapper<Health> mHealth;
    ComponentMapper<Heading> mHeading;
    ComponentMapper<Immobile> mImmobile;

    // should interval be per npc?
    public NPCAttackSystem(float interval) {
        super(Aspect.all(NPC.class, Hit.class, WorldPos.class, Heading.class), interval);
    }

    @Override
    protected void process(int npcId) {
        mapSystem
                .getNearEntities(npcId)
                .stream()
                // @todo este chequeo no funciona, getNearEntities debería garantizar que las entidades sean válidas (i.e. esten vivas)
                //.filter(playerId -> world.getEntity(e) != null)
                .filter(playerId -> !mNPC.has(playerId))
                .filter(playerId -> mWorldPos.has(playerId))
                .filter(playerId -> mHealth.get(playerId).getMin() > 0)
                .filter(playerId -> inRange(npcId, playerId))
                .findFirst()
                .ifPresent(targetId -> combatSystem.entityAttack(npcId, Optional.of(targetId)));

    }

    private boolean inRange(int npcId, int targetId) {
        WorldUtils worldUtils = WorldUtils(world);
        WorldPos npcPos = mWorldPos.get(npcId);
        WorldPos targetPos = mWorldPos.get(targetId);
        WorldPos facingPos = worldUtils.getFacingPos(npcPos, mHeading.get(npcId));
        if (worldUtils.distance(npcPos, targetPos) == 1) {
            if (!mImmobile.has(npcId) && !facingPos.equals(targetPos)) {
                // move heading
                mHeading.get(npcId).setCurrent(worldUtils.getHeading(npcPos, targetPos));
                EntityUpdate update = EntityUpdateBuilder.of(npcId).withComponents(mHeading.get(npcId)).build();
                entityUpdateSystem.add(update, UpdateTo.ALL);
                facingPos = targetPos;
            }
        }
        return facingPos.equals(targetPos);
    }
}
