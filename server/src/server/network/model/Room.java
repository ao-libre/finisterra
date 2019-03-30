package server.network.model;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Room {

    private final int id = ThreadLocalRandom.current().nextInt();
    private Set<Player> players = new HashSet<>();
    private int maxPlayers;

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

    public boolean hasPlayer(Player player) {
        return players.contains(player);
    }

    public void exit(Player player) {
        players.remove(player);
    }

    public Set<Player> getPlayers() {
        return players;
    }
}
