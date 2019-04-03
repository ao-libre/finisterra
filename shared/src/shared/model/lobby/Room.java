package shared.model.lobby;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Room {

    private final int id = ThreadLocalRandom.current().nextInt();
    private Set<Player> players = new HashSet<>();
    private int maxPlayers;

    private Room() {}

    public Room(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getId() {
        return id;
    }

    public boolean add(Player player) {
        if (players.size() < maxPlayers) {
            players.add(player);
        }
        return players.size() <= maxPlayers;
    }

    public void remove(Player player) {
        players.remove(player);
    }

    public boolean has(Player player) {
        return players.contains(player);
    }

    public Set<Player> getPlayers() {
        return players;
    }

    @Override
    public String toString() {
        return "Room: " + players.size() + "/" + maxPlayers;
    }
}
