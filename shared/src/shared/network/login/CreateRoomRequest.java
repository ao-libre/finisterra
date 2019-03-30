package shared.network.login;

public class CreateRoomRequest {

    private int maxPlayers;

    public CreateRoomRequest(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }
}
