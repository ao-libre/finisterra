package server.systems.world.entity.movement;

import com.artemis.ComponentMapper;
import com.esotericsoftware.minlog.Log;
import component.entity.character.states.Heading;
import component.entity.character.states.Immobile;
import component.movement.Destination;
import component.physics.AOPhysics;
import component.position.WorldPos;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import server.systems.network.EntityUpdateSystem;
import server.systems.network.ServerSystem;
import server.systems.world.MapSystem;
import server.systems.world.WorldEntitiesSystem;
import server.systems.world.entity.factory.EffectEntitySystem;
import server.utils.UpdateTo;
import server.utils.WorldUtils;
import shared.interfaces.FXs;
import shared.model.map.Map;
import shared.model.map.Tile;
import shared.model.map.WorldPosition;
import shared.network.movement.MovementNotification;
import shared.network.movement.MovementResponse;
import shared.util.EntityUpdateBuilder;

import java.util.Optional;

// TODO refactor: make active system processing entities with Moving component
public class MovementSystem extends PassiveSystem {

    // Injected systems
    private ServerSystem serverSystem;
    private WorldEntitiesSystem worldEntitiesSystem;
    private MapSystem mapSystem;
    private EntityUpdateSystem entityUpdateSystem;
    private EffectEntitySystem effectEntitySystem;

    ComponentMapper<Heading> mHeading;
    ComponentMapper<Immobile> mImmobile;
    ComponentMapper<WorldPos> mWorldPos;

    public void move(int playerId, int movementIndex, int requestNumber) {
        // Se fija donde esta el player y hacia donde quiere ir
        WorldUtils worldUtils = WorldUtils.WorldUtils(world);
        AOPhysics.Movement movement = AOPhysics.Movement.values()[movementIndex];

        Heading heading = mHeading.get(playerId);
        heading.setCurrent(worldUtils.getHeading(movement));

        WorldPos worldPos = mWorldPos.get(playerId);
        WorldPos oldPos = new WorldPos(worldPos);
        WorldPos nextPos = worldUtils.getNextPos(worldPos, movement);
        Map map = mapSystem.getMap(nextPos.map);

        // Hay un bloqueo?
        boolean blocked = mapSystem.getHelper().isBlocked(map, nextPos);

        // Hay un jugador en esta pos?
        boolean occupied = mapSystem.getHelper().hasEntity(mapSystem.getNearEntities(playerId), nextPos);

        // Obtengo prox. pos disponible
        if (!(mImmobile.has(playerId) || blocked || occupied)) {
            Tile tile = mapSystem.getMap(nextPos.map).getTile(nextPos.x, nextPos.y);
            WorldPosition tileExit = tile.getTileExit();
            if (tileExit != null) {
                Log.info("Moving to exit tile: " + tileExit);
                nextPos = new WorldPos(tileExit.getX(), tileExit.getY(), tileExit.getMap());
            }
            worldPos.map = nextPos.map;
            worldPos.x = nextPos.x;
            worldPos.y = nextPos.y;
        } else {
            nextPos = oldPos;
        }

        mapSystem.movePlayer(playerId, Optional.of(oldPos));

        // notify near users
        if (!nextPos.equals(oldPos)) {
            if (nextPos.map != oldPos.map) {
                entityUpdateSystem.add(EntityUpdateBuilder.of(playerId).withComponents(worldPos).build(), UpdateTo.NEAR);
            } else {
                // TODO convert notification into component.entity update
                worldEntitiesSystem.notifyToNearEntities(playerId, new MovementNotification(playerId, new Destination(nextPos, movement.ordinal())));
            }
        } else {
            entityUpdateSystem.add(EntityUpdateBuilder.of(playerId).withComponents(heading).build(), UpdateTo.NEAR);
        }

        // notify user
        serverSystem.sendByPlayerId(playerId, new MovementResponse(requestNumber, nextPos));
    }

    public void teleport(int playerId, WorldPos worldPos, WorldPos targetWorldPos) {
        effectEntitySystem.addFX(playerId, FXs.FX_TELEPORT, 1);
        worldPos.setWorldPos(targetWorldPos);
        EntityUpdateBuilder resetUpdate = EntityUpdateBuilder.of(playerId);
        resetUpdate.withComponents( worldPos );
        worldEntitiesSystem.notifyUpdate( playerId, resetUpdate.build() );
    }
}
