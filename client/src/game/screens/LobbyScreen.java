package game.screens;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import game.systems.lobby.LobbySystem;
import game.systems.network.ClientSystem;
import shared.model.lobby.Room;
import shared.network.lobby.CreateRoomRequest;
import shared.network.lobby.JoinRoomRequest;

@Wire
public class LobbyScreen extends AbstractScreen {

    private ClientSystem clientSystem;
    private LobbySystem lobbySystem;

    private List<Room> roomList;

    @Override
    protected void createUI() {
        roomList = new List<>(getSkin());

        Table container = new Table(getSkin());
        TextButton createRoomButton = new TextButton("CREATE ROOM", getSkin());
        createRoomButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                clientSystem.send(new CreateRoomRequest());
            }
        });

        TextButton joinRoomButton = new TextButton("JOIN ROOM", getSkin());
        joinRoomButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Room selected = roomList.getSelected();
                if (selected != null && !selected.isFull()) {
                    clientSystem.send(new JoinRoomRequest(selected.getId()));
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

    @Override
    public void render(float delta) {
        //@todo no hace falta actualizar en todos los frames
        updateRooms();

        super.render(delta);
    }

    private void updateRooms() {
        roomList.setItems(new Array<>(lobbySystem.getRooms().toArray()));
    }
}
