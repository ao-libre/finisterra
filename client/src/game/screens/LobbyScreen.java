package game.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import game.AOGame;
import game.handlers.AOAssetManager;
import game.systems.network.ClientSystem;
import shared.model.lobby.Player;
import shared.model.lobby.Room;
import shared.network.lobby.CreateRoomRequest;
import shared.network.lobby.JoinRoomRequest;
import shared.util.Messages;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class LobbyScreen extends AbstractScreen {

    private final Player player;
    private ClientSystem clientSystem;
    private Set<Room> rooms;
    private List<Room> roomList;

    public LobbyScreen(ClientSystem clientSystem, Player player, Room[] rooms) {
        super();
        this.clientSystem = clientSystem;
        this.player = player;
        this.rooms = Arrays.stream(rooms).collect(Collectors.toSet());
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

    public void roomMaxLimit() {
        AOAssetManager assetManager = AOGame.getGlobalAssetManager();

        Dialog dialog = new Dialog(assetManager.getMessages(Messages.MAX_ROOM_LIMIT_CREATION_TITLE), getSkin());
        dialog.text(assetManager.getMessages(Messages.MAX_ROOM_LIMIT_CREATION_DESCRIPTION));
        dialog.button("OK");
        dialog.show(getStage());
    }

    @Override
    protected void keyPressed(int keyCode) {

    }

    @Override
    void createContent() {
        roomList = new List<>(getSkin());

        Table container = new Table(getSkin());
        TextButton createRoomButton = new TextButton("CREATE ROOM", getSkin());
        createRoomButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                clientSystem.getKryonetClient().sendToAll(new CreateRoomRequest());
            }
        });

        TextButton joinRoomButton = new TextButton("JOIN ROOM", getSkin());
        joinRoomButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Room selected = roomList.getSelected();
                if (selected != null && !selected.isFull()) {
                    clientSystem.getKryonetClient().sendToAll(new JoinRoomRequest(selected.getId()));
                }
            }
        });

        container.add(roomList).width(400).height(400);
        container.row();
        container.add(joinRoomButton);
        container.row();
        container.add(createRoomButton);
        container.getColor().a = 0.8f;
        getMainTable().add(container);

    }

    private void updateRooms() {
        roomList.setItems(new Array<>(rooms.toArray()));
    }

    public ClientSystem getClientSystem() {
        return clientSystem;
    }

}
