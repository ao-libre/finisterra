package game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import game.systems.network.ClientSystem;
import shared.model.lobby.Player;
import shared.model.lobby.Room;
import shared.network.lobby.StartGameRequest;

public class RoomScreen extends AbstractScreen {
    private ClientSystem clientSystem;
    private Room room;
    private Player me;
    private List<Player> playerList;

    public RoomScreen(ClientSystem clientSystem, Room room, Player me) {
        super();
        this.clientSystem = clientSystem;
        this.room = room;
        this.me = me;
        updatePlayers();
    }

    public Player getPlayer() {
        return me;
    }

    public void updatePlayers() {
        playerList.setItems(room.getPlayers().toArray(new Player[0]));
    }

    public Room getRoom() {
        return room;
    }

    @Override
    void createContent() {
        playerList = new List<>(getSkin());
        TextButton start = new TextButton("START GAME", getSkin());
        start.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                clientSystem.getKryonetClient().sendToAll(new StartGameRequest(room.getId()));
            }
        });

        getMainTable().add(playerList).width(Gdx.graphics.getWidth() - (Gdx.graphics.getWidth() * 0.1f)).height(400);
        getMainTable().row();
        getMainTable().add(start);
    }

    @Override
    public void dispose() {
        clientSystem.stop();
        super.dispose();
    }
}
