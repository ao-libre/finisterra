package server.network;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.esotericsoftware.minlog.Log;
import server.core.Finisterra;
import server.database.Account;
import server.systems.FinisterraSystem;
import shared.interfaces.Hero;
import shared.model.lobby.Lobby;
import shared.model.lobby.Player;
import shared.model.lobby.Room;
import shared.model.lobby.Team;
import shared.network.account.AccountCreationRequest;
import shared.network.account.AccountCreationResponse;
import shared.network.account.AccountLoginRequest;
import shared.network.account.AccountLoginResponse;
import shared.network.interfaces.DefaultRequestProcessor;
import shared.network.lobby.*;
import shared.network.lobby.player.ChangeHeroRequest;
import shared.network.lobby.player.ChangePlayerNotification;
import shared.network.lobby.player.ChangeReadyStateRequest;
import shared.network.lobby.player.ChangeTeamRequest;
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
        Player player = new Player(connectionId, playerName);
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
        Player player = networkManager.getPlayerByConnection(connectionId);

        Lobby lobby = getLobby();
        Room room = lobby.createRoom();

        CreateRoomResponse response = new CreateRoomResponse(room, player);
        networkManager.sendTo(connectionId, response);

        if (response.getStatus() == CreateRoomResponse.Status.CREATED) {
            lobby.joinRoom(room.getId(), player);
            lobby.getWaitingPlayers()
                    .stream()
                    .filter(waitingPlayer -> !player.equals(waitingPlayer))
                    .filter(waitingPlayer -> networkManager.playerHasConnection(waitingPlayer))
                    .forEach(waitingPlayer -> networkManager.sendTo(networkManager.getConnectionByPlayer(waitingPlayer), new NewRoomNotification(room)));
        }
    }

    @Override
    public void processRequest(JoinRoomRequest joinRoomRequest, int connectionId) {
        Lobby lobby = getLobby();
        Optional<Room> room = lobby.getRoom(joinRoomRequest.getId());
        room.ifPresent(room1 -> {
            if (room1.isFull()) return; // @todo responder al cliente que la sala está llena
            Player player = networkManager.getPlayerByConnection(connectionId);
            player.setTeam(getBalancedTeam(room1));
            lobby.joinRoom(joinRoomRequest.getId(), player);
            room1.getPlayers().forEach(roomPlayer -> {
                int roomPlayerConnection = networkManager.getConnectionByPlayer(roomPlayer);
                networkManager.sendTo(roomPlayerConnection, new JoinRoomNotification(player, true));
            });
            networkManager.sendTo(connectionId, new JoinRoomResponse(room1, player));
        });
    }

    private Team getBalancedTeam(Room room) {
        long chaosCount = room.getPlayers().stream().filter(player -> player.getTeam().equals(Team.CAOS_ARMY)).count();
        long realCount = room.getPlayers().stream().filter(player -> player.getTeam().equals(Team.REAL_ARMY)).count();
        return chaosCount > realCount ? Team.REAL_ARMY : Team.CAOS_ARMY;
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
    public void processRequest(ChangeHeroRequest changeHeroRequest, int connectionId) {
        Hero hero = changeHeroRequest.getHero();
        Player player = networkManager.getPlayerByConnection(connectionId);
        player.setHero(hero);
        notifyPlayerChanged(player);
    }

    public void notifyPlayerChanged(Player player) {
        Lobby lobby = getLobby();
        lobby
                .getRooms()
                .stream()
                .filter(room -> room.has(player))
                .findFirst()
                .ifPresent(room -> room.getPlayers().forEach(roomPlayer -> {
                    int roomPlayerConnection = networkManager.getConnectionByPlayer(roomPlayer);
                    networkManager.sendTo(roomPlayerConnection, new ChangePlayerNotification(player));
                }));
    }

    @Override
    public void processRequest(ChangeReadyStateRequest changeReadyStateRequest, int connectionId) {
        Player player = networkManager.getPlayerByConnection(connectionId);
        player.setReady(!player.isReady());
        notifyPlayerChanged(player);
    }

    @Override
    public void processRequest(ChangeTeamRequest changeTeamRequest, int connectionId) {
        Player player = networkManager.getPlayerByConnection(connectionId);
        Team team = player.getTeam();
        switch (team) {
            case NO_TEAM:
            case CAOS_ARMY:
                player.setTeam(Team.REAL_ARMY);
                break;
            case REAL_ARMY:
                player.setTeam(Team.CAOS_ARMY);
                break;
        }
        notifyPlayerChanged(player);
    }

    @Override
    public void processRequest(StartGameRequest startGameRequest, int connectionId) {
        ApplicationListener applicationListener = Gdx.app.getApplicationListener();
        Finisterra finisterra = (Finisterra) applicationListener;
        Optional<Room> room = finisterra.getLobby().getRoom(startGameRequest.getRoomId());
        room.ifPresent(room1 -> {
            if (room1.getPlayers().stream().allMatch(Player::isReady)) {
                finisterra.startGame(room1);
            } // @todo else responder al cliente que los jugadores no están listos
        });
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

    @Override
    public void processRequest(AccountCreationRequest accountCreationRequest, int connectionId) {
        String email = accountCreationRequest.getEmail();
        String password = accountCreationRequest.getPassword();
        String salt = accountCreationRequest.getSalt();

        try {
            Account account = new Account(email, password, salt);
            account.save();
        } catch (Exception ex) {
            Log.info("Creacion de cuentas", "No se pudo crear la cuenta: " + email, ex);
        }

        networkManager.sendTo(connectionId, new AccountCreationResponse(success));
    }

    @Override
    public void processRequest(AccountLoginRequest accountLoginRequest, int connectionId) {
        String email = accountLoginRequest.getEmail();
        String password = accountLoginRequest.getPassword();

        // Obtenemos la cuenta de la carpeta Accounts.
        Account requestedAccount = Account.load(email);

        boolean success;
        success = (requestedAccount != null) && (requestedAccount.getPassword().equals(password));

        networkManager.sendTo(connectionId, new AccountLoginResponse(success));
    }
}
