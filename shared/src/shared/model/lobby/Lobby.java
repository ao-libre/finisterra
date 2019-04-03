package shared.model.lobby;

import shared.network.lobby.CreateRoomRequest;

import java.util.*;

public class Lobby {

    private Map<Integer, Room> rooms = new HashMap<>();
    private Set<Player> waitingPlayers = new HashSet<>();

    public Room createRoom(CreateRoomRequest roomRequest) {
        Room room = new Room(roomRequest.getMaxPlayers());
        rooms.put(room.getId(), room);
        return room;
    }

    public boolean joinRoom(int id, Player player) {
        Room room = rooms.get(id);
        removeWaitingPlayer(player);
        return room.add(player);
    }

    public boolean exitRoom(Player player) {
        Optional<Room> room = rooms //
                .values() //
                .stream() //
                .filter(r -> r.has(player))
                .findFirst();

        room.ifPresent(r -> r.remove(player));
        return room.isPresent();
    }

    public Collection<Room> getRooms() {
        return rooms.values();
    }

    public Optional<Room> getRoom(int id) {
        return Optional.ofNullable(rooms.get(id));
    }

    public void addWaitingPlayer(Player player) {
        waitingPlayers.add(player);
    }

    public void removeWaitingPlayer(Player player) {
        waitingPlayers.remove(player);
    }

    public Set<Player> getWaitingPlayers() {
        return waitingPlayers;
    }

    public void playerDisconnected(Player player) {
        // TODO Notify all
        if (!exitRoom(player)) {
            removeWaitingPlayer(player);
        }
    }
}
