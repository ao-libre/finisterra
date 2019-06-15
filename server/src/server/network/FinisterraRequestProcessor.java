package server.network;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import server.core.Finisterra;
import server.systems.FinisterraSystem;
import shared.model.lobby.Lobby;
import shared.model.lobby.Player;
import shared.model.lobby.Room;
import shared.model.lobby.Team;
import shared.network.interfaces.DefaultRequestProcessor;
import shared.network.lobby.*;
import shared.network.time.TimeSyncRequest;
import shared.network.time.TimeSyncResponse;

import java.util.Optional;

/**
 * Every packet received from users will be processed here
 */
public class FinisterraRequestProcessor extends DefaultRequestProcessor {

    private FinisterraSystem networkManager;

    @Override
    public void processRequest(JoinLobbyRequest joinLobbyRequest, int connectionId) {
        String playerName = joinLobbyRequest.getPlayerName();
        Player player = new Player(connectionId, playerName, joinLobbyRequest.getHero());
        Lobby lobby = getLobby();
        lobby.addWaitingPlayer(player);
        networkManager.registerUserConnection(player, connectionId);
        networkManager.sendTo(connectionId, new JoinLobbyResponse(player, lobby.getRooms().toArray(new Room[0])));
    }

    private Lobby getLobby() {
        ApplicationListener applicationListener = Gdx.app.getApplicationListener();
        Finisterra finisterra = (Finisterra) applicationListener;
        return finisterra.getLobby();
    }

    @Override
    public void processRequest(CreateRoomRequest createRoomRequest, int connectionId) {
        Lobby lobby = getLobby();
        Room room = lobby.createRoom(createRoomRequest);
        Player player = networkManager.getPlayerByConnection(connectionId);
        lobby.joinRoom(room.getId(), player);
        networkManager.sendTo(connectionId,
                new CreateRoomResponse(room, player));
        lobby.getWaitingPlayers()
                .stream()
                .filter(waitingPlayer -> !player.equals(waitingPlayer))
                .filter(waitingPlayer -> networkManager.playerHasConnection(waitingPlayer))
                .forEach(waitingPlayer -> networkManager.sendTo(networkManager.getConnectionByPlayer(waitingPlayer), new NewRoomNotification(room)));
    }

    @Override
    public void processRequest(JoinRoomRequest joinRoomRequest, int connectionId) {
        Lobby lobby = getLobby();
        Optional<Room> room = lobby.getRoom(joinRoomRequest.getId());
        room.ifPresent(room1 -> {
            Player player = networkManager.getPlayerByConnection(connectionId);
            player.setTeam(Team.NO_TEAM);
            room1.getPlayers().forEach(roomPlayer -> {
                int roomPlayerConnection = networkManager.getConnectionByPlayer(roomPlayer);
                networkManager.sendTo(roomPlayerConnection, new JoinRoomNotification(player, true));
            });
            lobby.joinRoom(joinRoomRequest.getId(), player);
            networkManager.sendTo(connectionId, new JoinRoomResponse(room1, player));
        });
    }

    @Override
    public void processRequest(ExitRoomRequest exitRoomRequest, int connectionId) {
        Player player = networkManager.getPlayerByConnection(connectionId);
        Lobby lobby = getLobby();
        lobby
                .getRooms()
                .stream()
                .filter(room -> room.has(player))
                .findFirst()
                .ifPresent(room -> room.getPlayers().forEach(roomPlayer -> {
                    int roomPlayerConnection = networkManager.getConnectionByPlayer(roomPlayer);
                    networkManager.sendTo(roomPlayerConnection, new JoinRoomNotification(player, false));
                }));
        lobby.exitRoom(player);
    }

    @Override
    public void processRequest(StartGameRequest startGameRequest, int connectionId) {
        ApplicationListener applicationListener = Gdx.app.getApplicationListener();
        Finisterra finisterra = (Finisterra) applicationListener;
        Optional<Room> room = finisterra.getLobby().getRoom(startGameRequest.getRoomId());
        room.ifPresent(finisterra::startGame);
    }

    @Override
    public void processRequest(TimeSyncRequest request, int connectionId) {
        long receiveTime = System.nanoTime();
        TimeSyncResponse response = new TimeSyncResponse();
        response.receiveTime = receiveTime;
        response.requestId = request.requestId;
        response.sendTime = System.nanoTime();
        networkManager.sendTo(connectionId, response);
    }
}
