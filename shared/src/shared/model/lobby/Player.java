package shared.model.lobby;

import shared.interfaces.Hero;

import java.util.concurrent.ThreadLocalRandom;

public class Player {

    private int connectionId;
    private String playerName;
    private Team team;
    private Hero hero;
    private boolean ready;

    private Player() {}

    public Player(int connectionId, String playerName) {
        this.connectionId = connectionId;
        this.playerName = playerName;
        this.hero = Hero.values()[ThreadLocalRandom.current().nextInt(Hero.values().length)];
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getConnectionId() {
        return connectionId;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Hero getHero() {
        return hero;
    }

    public boolean isReady() {
        return ready;
    }

    public void setHero(Hero hero) {
        this.hero = hero;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    @Override
    public String toString() {
        return getPlayerName() + " Team: " + getTeam().toString() + " Ready: " + isReady();
    }
}
