package server.network.model;

import shared.network.login.CreateRoomRequest;
import shared.network.login.JoinRoomRequest;

import java.util.*;
import java.util.stream.Collectors;

public class Lobby {

    private Map<Integer, Room> rooms = new HashMap<>();
    private Set<Player> waitingPlayers = new HashSet<>();

    public void createRoom(CreateRoomRequest roomRequest) {
        Room room = new Room(roomRequest.getMaxPlayers());
        rooms.put(room.getId(), room);
        notify(room);
    }

    private void notify(Room room) {
        waitingPlayers.forEach(player -> {
            // TODO notify user
        });
    }

    public boolean joinRoom(JoinRoomRequest joinRequest, Player player) {
        Room room = rooms.get(joinRequest.getId());
        return room.add(player);
    }

    public boolean exitRoom(Player player) {
        Optional<Room> room = rooms //
                .values() //
                .stream() //
                .filter(r -> r.hasPlayer(player))
                .findFirst();

        room.ifPresent(r -> r.exit(player));
        return room.isPresent();
    }

    public Set<Room> getRooms() {
        return rooms.values().stream().collect(Collectors.toSet());
    }

    public void addWaitingPlayer(Player player) {
        waitingPlayers.add(player);
    }
}
