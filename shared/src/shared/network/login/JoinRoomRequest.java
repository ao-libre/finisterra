package shared.network.login;

public class JoinRoomRequest {

    private int id;

    public JoinRoomRequest(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
