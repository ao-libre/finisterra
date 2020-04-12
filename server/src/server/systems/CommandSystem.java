package server.systems;

import com.artemis.annotations.Wire;
import com.esotericsoftware.minlog.Log;
import component.console.ConsoleMessage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import server.systems.manager.DefaultManager;
import server.systems.manager.WorldManager;
import server.systems.network.MessageSystem;
import shared.network.interaction.TalkRequest;
import shared.util.Messages;

import java.util.HashMap;
import java.util.function.Consumer;

@Wire
public class CommandSystem extends DefaultManager {

    // Injected Systems
    private ServerSystem networkManager;
    private WorldManager worldManager;
    private MessageSystem messageSystem;

    private final HashMap<String, Consumer<Command>> commands = new HashMap<>();

    /**
     * Aca se prepara una lista de comandos disponibles para usar desde el cliente.
     *
     * @see server.network.ServerRequestProcessor#processRequest(TalkRequest, int)
     */
    public CommandSystem() {
        commands.put("online", (commandStructure) -> {
            String connections = String.valueOf(networkManager.getAmountConnections());
            messageSystem.add(commandStructure.senderID, ConsoleMessage.info(Messages.PLAYERS_ONLINE.name(), connections));
        });
        commands.put("salir" , (commandStructure) -> {
            int connectionId = networkManager.getConnectionByPlayer(commandStructure.senderID);
            networkManager.disconnected(connectionId);
        });
		commands.put("die" , (commandStructure) -> {
            worldManager.entityDie(commandStructure.senderID);
        });
    }

    /**
     * Ejecuta el comando desde la lista {@link #commands}
     * @param command   String completo del comando.
     * @param senderID  Identificador del jugador.
     */
    public void handleCommand(@NotNull String command, int senderID) {
        CommandSystem.Command commandStructure = new CommandSystem.Command(senderID, command);
        try {
            commands.get(commandStructure.name).accept(commandStructure);
        } catch (Exception ex) {
            Log.error("Command Parser", "Error al ejecutar comando: " + command, ex);
        }
    }

    /**
     * Guardo el senderID y parseo de el comando separando el nombre del mismo y sus argumentos.
     * En otras palabras, lo preparo para usarlo en {@link #handleCommand(String, int)}
     */
    public static class Command {
        public String name;
        public int senderID;
        public String[] params;

        @Contract(pure = true)
        public Command(int senderID, @NotNull String fullCommandString) {
            this.senderID = senderID;

            String[] commandToParse = fullCommandString.split(" ");
            this.name = commandToParse[0].substring(1);
            this.params = commandToParse;
        }
    }
}
