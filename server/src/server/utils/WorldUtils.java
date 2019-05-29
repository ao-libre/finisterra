package server.utils;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.Bag;
import entity.character.states.Heading;
import physics.AOPhysics;
import position.WorldPos;
import shared.interfaces.Constants;

import java.util.ArrayList;
import java.util.List;

public class WorldUtils {

    private World world;

    private WorldUtils(World world) {
        this.world = world;
    }

    public static WorldUtils WorldUtils(World world) {
        return new WorldUtils(world);
    }

    public int distance(WorldPos pos1, WorldPos pos2) {
        if (pos1 == null || pos2 == null) {
            return -1;
        }
        if (pos1.map != pos2.map) {
            return -1;
        }
        return Math.abs(pos1.x - pos2.x) + Math.abs(pos1.y - pos2.y);
    }

    public WorldPos getNextPos(WorldPos pos, AOPhysics.Movement movement) {
        return new WorldPos(
                (movement == AOPhysics.Movement.RIGHT ? 1 : movement == AOPhysics.Movement.LEFT ? -1 : 0) + pos.x,
                (movement == AOPhysics.Movement.UP ? -1 : movement == AOPhysics.Movement.DOWN ? 1 : 0) + pos.y,
                pos.map);
    }

    public WorldPos getFacingPos(WorldPos pos, Heading heading) {
        return new WorldPos(
                (heading.current == Constants.Heading.EAST.toInt() ? 1 : heading.current == Constants.Heading.WEST.toInt() ? -1 : 0) + pos.x,
                (heading.current == Constants.Heading.NORTH.toInt() ? -1 : heading.current == Constants.Heading.SOUTH.toInt() ? 1 : 0) + pos.y,
                pos.map);
    }

    public List<Component> getComponents(Entity player) {
        Bag<Component> components = player.getComponents(new Bag<>());
        List<Component> componentsToSend = new ArrayList<>();
        components.forEach(component -> {
            if (component != null) {
                componentsToSend.add(component);
            }
        });
        return componentsToSend;
    }

    public Component[] getComponents(int playerId) {
        List<Component> components = getComponents(world.getEntity(playerId));
        return components.toArray(new Component[0]);
    }

    public int getHeading(AOPhysics.Movement movement) {
        return movement == AOPhysics.Movement.UP ? Heading.HEADING_NORTH : movement == AOPhysics.Movement.DOWN ? Heading.HEADING_SOUTH : movement == AOPhysics.Movement.LEFT ? Heading.HEADING_WEST : Heading.HEADING_EAST;
    }

    public int getHeading(WorldPos pos1, WorldPos pos2) {
        return pos1.y > pos2.y ? Heading.HEADING_NORTH : pos1.y < pos2.y ? Heading.HEADING_SOUTH : pos1.x > pos2.x ? Heading.HEADING_WEST : Heading.HEADING_EAST;
    }
}
