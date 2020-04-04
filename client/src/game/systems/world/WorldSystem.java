package game.systems.world;

import com.artemis.annotations.Wire;
import component.entity.character.states.Heading;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import component.physics.AOPhysics;
import component.position.WorldPos;

@Wire
public class WorldSystem extends PassiveSystem {

    public int getHeading(AOPhysics.Movement movement) {
        return movement == AOPhysics.Movement.UP ? Heading.HEADING_NORTH : movement == AOPhysics.Movement.DOWN ? Heading.HEADING_SOUTH : movement == AOPhysics.Movement.LEFT ? Heading.HEADING_WEST : Heading.HEADING_EAST;
    }

    public int distance(WorldPos pos1, WorldPos pos2) {
        if (pos1 == null || pos2 == null) {
            return -1;
        }
        if (pos1.map != pos2.map) {
            return -1;
        }
        return Math.abs(getDistanceX(pos1, pos2) + (pos1.y - pos2.y));
    }

    public int getDistanceX(WorldPos pos1, WorldPos pos2) {
        return pos1.x - pos2.x;
    }
}
