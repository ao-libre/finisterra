package server.systems;

import com.artemis.annotations.Wire;
import server.systems.manager.DefaultManager;
import server.systems.manager.WorldManager;
import shared.network.notifications.ConsoleMessage;
import shared.util.Messages;

@Wire
public class CommandSystem extends DefaultManager {

    private static final String CMD_PLAYERS_COUNT = "/playersonline";

    // Injected Systems
    private ServerSystem networkManager;
    private WorldManager worldManager;

    public void handleCommand(String command, int senderId) {
        // Example command to get the online players
        if (command.equalsIgnoreCase(CMD_PLAYERS_COUNT)) {
            String connections = String.valueOf(networkManager.getAmountConnections());
            worldManager.sendEntityUpdate(senderId, ConsoleMessage.info(Messages.PLAYERS_ONLINE, connections));
        }
    }
}
