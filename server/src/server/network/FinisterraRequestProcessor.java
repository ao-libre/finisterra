package server.network;

import server.core.Finisterra;
import server.manager.LobbyNetworkManager;
import shared.model.lobby.Lobby;
import shared.model.lobby.Player;
import shared.model.lobby.Room;
import shared.model.lobby.Team;
import shared.network.interfaces.DefaultRequestProcessor;
import shared.network.lobby.*;

import java.util.Optional;

/**
 * Every packet received from users will be processed here
 */
public class FinisterraRequestProcessor extends DefaultRequestProcessor {

    private Finisterra finisterra;

    public FinisterraRequestProcessor(Finisterra finisterra) {
        this.finisterra = finisterra;
    }

    @Override
    public void processRequest(JoinLobbyRequest joinLobbyRequest, int connectionId) {
        String playerName = joinLobbyRequest.getPlayerName();
        Player player = new Player(connectionId, playerName, joinLobbyRequest.getHero());
        Lobby lobby = finisterra.getLobby();
        lobby.addWaitingPlayer(player);
        finisterra.getNetworkManager().registerUserConnection(player, connectionId);
        finisterra.getNetworkManager().sendTo(connectionId, new JoinLobbyResponse(player, lobby.getRooms().toArray(new Room[0])));
    }

    @Override
    public void processRequest(CreateRoomRequest createRoomRequest, int connectionId) {
        Lobby lobby = finisterra.getLobby();
        LobbyNetworkManager networkManager = finisterra.getNetworkManager();
        Room room = lobby.createRoom(createRoomRequest);
        Player player = finisterra.getNetworkManager().getPlayerByConnection(connectionId);
        finisterra.getLobby().joinRoom(room.getId(), player);
        networkManager.sendTo(connectionId,
                              new CreateRoomResponse(room, player));
        lobby.getWaitingPlayers()
            .stream()
            .filter(waitingPlayer -> !player.equals(waitingPlayer))
            .forEach(waitingPlayer -> {
                networkManager.sendTo(networkManager.getConnectionByPlayer(waitingPlayer), new NewRoomNotification(room));
            });
    }

    @Override
    public void processRequest(JoinRoomRequest joinRoomRequest, int connectionId) {
        Optional<Room> room = finisterra.getLobby().getRoom(joinRoomRequest.getId());
        room.ifPresent(room1 -> {
            Player player = finisterra.getNetworkManager().getPlayerByConnection(connectionId);
            player.setTeam(Team.NO_TEAM);
            room1.getPlayers().forEach(roomPlayer -> {
                int roomPlayerConnection = finisterra.getNetworkManager().getConnectionByPlayer(roomPlayer);
                finisterra.getNetworkManager().sendTo(roomPlayerConnection, new JoinRoomNotification(player, true));
            });
            finisterra.getLobby().joinRoom(joinRoomRequest.getId(), player);
            finisterra.getNetworkManager().sendTo(connectionId, new JoinRoomResponse(room1, player));
        });
    }

    @Override
    public void processRequest(ExitRoomRequest exitRoomRequest, int connectionId) {
        Player player = finisterra.getNetworkManager().getPlayerByConnection(connectionId);
        finisterra
            .getLobby()
            .getRooms()
            .stream()
            .filter(room -> room.has(player))
            .findFirst()
            .ifPresent(room -> {
                room.getPlayers().forEach(roomPlayer -> {
                    int roomPlayerConnection = finisterra.getNetworkManager().getConnectionByPlayer(roomPlayer);
                    finisterra.getNetworkManager().sendTo(roomPlayerConnection, new JoinRoomNotification(player, false));
                });
            });
        finisterra.getLobby().exitRoom(player);
    }

    @Override
    public void processRequest(StartGameRequest startGameRequest, int connectionId) {
        Optional<Room> room = finisterra.getLobby().getRoom(startGameRequest.getRoomId());
        room.ifPresent(room1 -> finisterra.startGame(room1));
    }
}
