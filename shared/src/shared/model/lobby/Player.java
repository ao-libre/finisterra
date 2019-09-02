package shared.model.lobby;

import shared.interfaces.Hero;

import java.util.Objects;
import java.util.Optional;

public class Player {

    private int connectionId;
    private String playerName;
    private Team team;
    private Hero hero;
    private boolean ready;

    public Player() {
    }

    public Player(int connectionId, String playerName, Hero hero) {
        this.connectionId = connectionId;
        this.playerName = playerName;
        this.hero = hero;
    }

    public Player(int connectionId, String playerName) {
        this(connectionId, playerName, Hero.getRandom());
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getConnectionId() {
        return connectionId;
    }

    public Team getTeam() {
        return Optional.ofNullable(team).orElse(Team.CAOS_ARMY);
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Hero getHero() {
        return hero;
    }

    public void setHero(Hero hero) {
        this.hero = hero;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    @Override
    public String toString() {
        return getPlayerName() + " is a " + getHero().name() + " and is " + (isReady() ? "ready :D! " : "not ready :(");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return connectionId == player.connectionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(connectionId);
    }
}
