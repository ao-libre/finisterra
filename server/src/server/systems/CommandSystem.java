package server.systems;

import com.artemis.E;
import com.artemis.annotations.Wire;
import server.systems.manager.DefaultManager;
import server.systems.manager.WorldManager;
import shared.network.notifications.ConsoleMessage;
import shared.util.Messages;

@Wire
public class CommandSystem extends DefaultManager {

    private static final String CMD_PLAYERS_COUNT = "/playersonline";
    private static final String CMD_PLAYER_SET_HOME_CITY = "/sethome";
    private static final String CMD_PLAYER_SEE_HOME_CITY = "/seehome";

    // Injected Systems
    private ServerSystem networkManager;
    private WorldManager worldManager;

    public void handleCommand(String command, int senderId) {
        // Example command to get the online players
        if (command.equalsIgnoreCase(CMD_PLAYERS_COUNT)) {
            String connections = String.valueOf(networkManager.getAmountConnections());
            worldManager.sendEntityUpdate(senderId, ConsoleMessage.info(Messages.PLAYERS_ONLINE, connections));
        }
        if (command.equalsIgnoreCase( CMD_PLAYER_SET_HOME_CITY )){
            E player = E.E(senderId);
            player.originPosMap(player.worldPosMap() ).originPosX(player.worldPosX()).originPosY(player.worldPosY());
        }
        if (command.equalsIgnoreCase( CMD_PLAYER_SEE_HOME_CITY )){
            E player = E.E(senderId);
            worldManager.sendEntityUpdate(senderId, ConsoleMessage.info( Messages.HOME_POS," " +
                    + player.originPosMap(), " " + player.originPosX(), " " + player.originPosY()));
        }

    }
}
