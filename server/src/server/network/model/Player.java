package server.network.model;

import shared.interfaces.Hero;

public class Player {

    private final int connectionId;
    private Team team;
    private Hero hero;
    private boolean ready;

    public Player(int connectionId) {
        this.connectionId = connectionId;
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
}
