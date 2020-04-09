package shared.model.npcs;

import com.artemis.E;
import com.artemis.World;
import component.entity.character.states.Heading;
import component.position.WorldPos;

public class NPCToEntity {

    public static int getNpcEntity(World world, int npcIndex, WorldPos pos, NPC npc) {
        int npcId = world.create();

        E npcEntity = E.E(npcId);
        npcEntity
                .nPCId(npcIndex)
                .bodyIndex(npc.getBody())
                .headingCurrent(Heading.HEADING_SOUTH)
                .nameText(npc.getName());
        if (npc.getMovement() == 3) {
            npcEntity.aOPhysics().aOPhysicsVelocity(85f);
            npcEntity.aIMovement();
        }
        if (npc.getHead() > 0) {
            npcEntity.headIndex(npc.getHead());
        }
        if (npc.isCommerce()) {
            npcEntity.commerce();
        }
        if (npc.isHostile()) {
            npcEntity.hostile();
        }
        if (npc.getMaxHit() > 0) {
            npcEntity.hit().hitMax(npc.getMaxHit()).hitMin(npc.getMinHit());
        }
        if (npc.getEvasionPower() > 0) {
            npcEntity.evasionPowerValue(npc.getEvasionPower());
        }
        if (npc.getAttackPower() > 0) {
            npcEntity.attackPowerValue(npc.getAttackPower());
        }
        if (npc.getMaxHP() > 0) {
            npcEntity.health().healthMin(npc.getMinHP()).healthMax(npc.getMaxHP());
        }
        if (npc.isAttackable()) {
            npcEntity.attackable();
        }
        if (!npc.getName().isBlank()) {
            npcEntity.nameText(npc.getName());
        }

        npcEntity.originPosMap(pos.map).originPosX(pos.x).originPosY(pos.y);
        npcEntity.worldPosMap(pos.map).worldPosX(pos.x).worldPosY(pos.y);
        return npcId;
    }
}
