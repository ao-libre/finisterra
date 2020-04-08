package game.systems.lobby;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.utils.Array;
import game.handlers.AOAssetManager;
import game.screens.ScreenManager;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import shared.model.lobby.Player;
import shared.model.lobby.Room;
import shared.util.Messages;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Wire //@todo crear e inyectar
public class LobbySystem extends PassiveSystem {

    private AOAssetManager assetManager;
    private ScreenManager screenManager;

    private Player player;
    private Set<Room> rooms;
    private Room currentRoom;

    public void init(Player player, Room[] rooms) {
        this.player = player;
    }

    public void setRooms(Room[] rooms) {
        this.rooms = Arrays.stream(rooms).collect(Collectors.toSet());
    }

    public Set<Room> getRooms() {
        return rooms;
    }

    public void setCurrentRoom(Room room) {
        currentRoom = room;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void roomMaxLimit() {
        Dialog dialog = new Dialog(assetManager.getMessages(Messages.MAX_ROOM_LIMIT_CREATION_TITLE), getSkin());
        dialog.text(assetManager.getMessages(Messages.MAX_ROOM_LIMIT_CREATION_DESCRIPTION));
        dialog.button("OK");
        dialog.show(screenManager.getScreen().getStage());
    }

    private void updateRooms() {
        roomList.setItems(new Array<>(rooms.toArray())); //@fixme
    }
}
