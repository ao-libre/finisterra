package ar.com.tamborindeguy.network;

import ar.com.tamborindeguy.database.model.User;
import ar.com.tamborindeguy.manager.MapManager;
import ar.com.tamborindeguy.manager.WorldManager;
import ar.com.tamborindeguy.network.interfaces.IRequestProcessor;
import ar.com.tamborindeguy.network.interfaces.IResponse;
import ar.com.tamborindeguy.network.login.LoginFailed;
import ar.com.tamborindeguy.network.login.LoginOK;
import ar.com.tamborindeguy.network.login.LoginRequest;
import ar.com.tamborindeguy.network.movement.MovementRequest;
import ar.com.tamborindeguy.network.movement.MovementResponse;
import ar.com.tamborindeguy.network.notifications.EntityUpdate;
import ar.com.tamborindeguy.util.MapUtils;
import ar.com.tamborindeguy.utils.WorldUtils;
import com.artemis.Component;
import com.artemis.E;
import com.artemis.Entity;
import position.WorldPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.artemis.E.E;

public class ServerRequestProcessor implements IRequestProcessor {

    @Override
    public void processRequest(LoginRequest request, int connectionId) {
        IResponse response;
        User user = WorldManager.getUser(request.username);
        if (user == null) {
            response = new LoginFailed("User doesn't exists");
//        } else if (user.getPassword().equals(request.password)) {
        } else {
            final Entity entity = WorldManager.createEntity(user, connectionId);
            int map = E(entity).getWorldPos().map;
            NetworkComunicator.sendTo(connectionId, new EntityUpdate(entity.getId(), WorldUtils.getComponents(entity)));
            NetworkComunicator.sendTo(connectionId, new LoginOK(entity.getId()));
            WorldManager.registerEntity(connectionId, entity.getId());
//        } else {
//            response = new LoginFailed("Wrong password");
        }
//        NetworkComunicator.sendTo(connectionId, response);
    }

    @Override
    public void processRequest(MovementRequest request, int connectionId) {
        // TODO check map changed

        // validate if valid
        int playerId = NetworkComunicator.getPlayerByConnection(connectionId);

        // update server entity
        E player = E(playerId);

        player.headingCurrent(WorldUtils.getHeading(request.movement));

        WorldPos worldPos = player.getWorldPos();
        WorldPos oldPos = new WorldPos(worldPos);
        WorldPos nextPos = WorldUtils.getNextPos(worldPos, request.movement);
        boolean blocked = MapUtils.isBlocked(MapManager.get(nextPos.map), nextPos);
        boolean occupied = MapUtils.hasEntity(MapManager.getNearEntities(playerId), nextPos);
        if (player.hasImmobile() || blocked || occupied) {
            nextPos = worldPos;
        }

        player.worldPosMap(nextPos.map);
        player.worldPosX(nextPos.x);
        player.worldPosY(nextPos.y);
        player.destinationDir(request.movement);
        player.destinationWorldPos(player.getWorldPos());

        MapManager.movePlayer(playerId, Optional.of(oldPos));

        // notify near users
        List<Component> components = new ArrayList<>();
        components.addAll(Arrays.asList(player.getHeading(), player.getWorldPos(), player.getDestination()));
        WorldManager.notifyUpdateToNearEntities(playerId, components);

        // notify user
        NetworkComunicator.sendTo(connectionId, new MovementResponse(request.requestNumber, nextPos));

    }

}
