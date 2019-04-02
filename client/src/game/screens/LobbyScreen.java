package game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import game.systems.network.ClientSystem;
import shared.model.lobby.Player;
import shared.model.lobby.Room;
import shared.network.lobby.CreateRoomRequest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class LobbyScreen extends AbstractScreen {

    private ClientSystem clientSystem;
    private final Player player;
    private Set<Room> rooms;
    private List<Room> roomList;

    public LobbyScreen(ClientSystem clientSystem, Player player, Room[] rooms) {
        super();
        this.clientSystem = clientSystem;
        this.player = player;
        this.rooms = new HashSet<>(Arrays.stream(rooms).collect(Collectors.toSet()));
        updateRooms();
    }

    public void roomCreated(Room room) {
        rooms.add(room);
        updateRooms();
    }

    public void roomClosed(Room room) {
        rooms.remove(room);
        updateRooms();
    }

    @Override
    void createContent() {
        roomList = new List<>(getSkin());

        TextButton createRoomButton = new TextButton("CREATE ROOM", getSkin());
        createRoomButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                clientSystem.getKryonetClient().sendToAll(new CreateRoomRequest(10));
            }
        });

        getMainTable().add(roomList).width(400).height(400);
        getMainTable().row();
        getMainTable().add(createRoomButton);

    }

    private void updateRooms() {
        roomList.setItems(new Array(rooms.toArray()));
    }

    public ClientSystem getClientSystem() {
        return clientSystem;
    }
}
