package server.systems.network;

import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.minlog.Log;
import component.console.ConsoleMessage;
import component.position.WorldPos;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import server.systems.world.MapSystem;
import server.systems.world.WorldEntitiesSystem;
import server.utils.CityMapsNumbers;
import shared.network.interaction.TalkRequest;
import shared.util.EntityUpdateBuilder;
import shared.util.Messages;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Wire
public class CommandSystem extends PassiveSystem {

    private final Map<String, Consumer<Command>> commands = new HashMap<>();
    // Injected Systems
    private ServerSystem networkManager;
    private MapSystem mapSystem;
    private WorldEntitiesSystem worldEntitiesSystem;
    private MessageSystem messageSystem;

    /**
     * Aca se prepara una lista de comandos disponibles para usar desde el cliente.
     *
     * @see ServerRequestProcessor#processRequest(TalkRequest, int)
     */
    public CommandSystem() {
        commands.put("online", (commandStructure) -> {
            String connections = String.valueOf(networkManager.getAmountConnections());
            messageSystem.add(commandStructure.senderID, ConsoleMessage.info(Messages.PLAYERS_ONLINE.name(), connections));
        });
        commands.put("salir", (commandStructure) -> {
            int connectionId = networkManager.getConnectionByPlayer(commandStructure.senderID);
            networkManager.disconnected(connectionId);
        });
        commands.put("die", (commandStructure) -> worldEntitiesSystem.entityDie(commandStructure.senderID));
        commands.put("sethome", (commandStructure) -> {
            int senderId = commandStructure.senderID;
            final int capacity = 17;
            Array<Integer> cityMaps = new Array<>(capacity);

            cityMaps.add(CityMapsNumbers.ABADIA_LINDOS);
            cityMaps.add(CityMapsNumbers.AFUERAS_BANDERBILL);
            cityMaps.add(CityMapsNumbers.ARGHAL_OESTE);
            cityMaps.add(CityMapsNumbers.CENTRO_ARGHAL);
            cityMaps.add(CityMapsNumbers.CENTRO_BANDERBILL);
            cityMaps.add(CityMapsNumbers.CENTRO_LINDOS);
            cityMaps.add(CityMapsNumbers.CIUDAD_ARKHEIN);
            cityMaps.add(CityMapsNumbers.CIUDAD_BANDERBILL);
            cityMaps.add(CityMapsNumbers.CIUDAD_LINDOS);
            cityMaps.add(CityMapsNumbers.CIUDAD_NUEVA_ESPERANZA);
            cityMaps.add(CityMapsNumbers.MUELLES_ARGHAL);
            cityMaps.add(CityMapsNumbers.MUELLES_BANDERBILL);
            cityMaps.add(CityMapsNumbers.NEMAHUAK);
            cityMaps.add(CityMapsNumbers.NIX);
            cityMaps.add(CityMapsNumbers.PUENTES_ARKHEIN);
            cityMaps.add(CityMapsNumbers.PUERTO_ARKHEIN);
            cityMaps.add(CityMapsNumbers.ULLATHORPE);

            E player = E.E(senderId);
            int playerMap = player.worldPosMap(), playerX = player.worldPosX(), playerY = player.worldPosY();
            boolean homeSet = false;
            int i = 0;
            while ((i < capacity) && !homeSet) {
                if (playerMap == cityMaps.get(i)) {
                    player.originPosMap(playerMap).originPosX(playerX).originPosY(playerY);
                    messageSystem.add(senderId, ConsoleMessage.info("HOME_SET"));
                    homeSet = true;
                }
                i++;
            }
            if (!homeSet) {
                messageSystem.add(senderId, ConsoleMessage.info("ONLY_MAPS", "" + cityMaps));
            }
        });
        commands.put("seehome", (commandStructure) -> {
            E player = E.E(commandStructure.senderID);
            messageSystem.add(commandStructure.senderID, ConsoleMessage.info("HOME_POS",
                    String.valueOf(player.originPosMap()), String.valueOf(player.originPosX()), String.valueOf(player.originPosY())));
        });
        commands.put("resurrect", (commandStructure) -> {
            int senderId = commandStructure.senderID;
            E player = E.E(senderId);
            if (player.healthMin() == 0) {
                worldEntitiesSystem.resurrectRequest(senderId);
                messageSystem.add(senderId, ConsoleMessage.info("TIME_TO_RESURRECT"));
            } else {
                messageSystem.add(senderId, ConsoleMessage.info("YOU_ARE_ALIVE"));
            }
        });
        commands.put("tp", (command) -> {
            int senderID = command.senderID;
            E player = E.E(senderID);
            int map = Integer.parseInt(command.params[1]);
            int x = Integer.parseInt(command.params[2]);
            int y = Integer.parseInt(command.params[3]);
            if (mapSystem.getHelper().isValid(new WorldPos(x, y, map))) {
                player.worldPosMap(map).worldPosX(x).worldPosY(y);
                EntityUpdateBuilder resetUpdate = EntityUpdateBuilder.of(senderID);
                resetUpdate.withComponents(player.getWorldPos());
                worldEntitiesSystem.notifyUpdate(senderID, resetUpdate.build());
            }
        });
    }

    /**
     * Ejecuta el comando desde la lista {@link #commands}
     *
     * @param command  String completo del comando.
     * @param senderID Identificador del jugador.
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
     * Se fija si el comando esta declarado en {@link #commands}
     *
     * @param command comando a evaluar.
     * @return boolean Si el comando existe o no en {@link #commands}
     */
    public boolean commandExists(@NotNull String command) {
        String[] presumedCommand = command.split(" ");
        return commands.containsKey(presumedCommand[0].substring(1));
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

        /**
         * Se fija si el string pasado como parametro tiene:
         * - El prefijo caracteristico que poseen los comandos.
         *
         * @param message Mensaje a analizar.
         * @return boolean Si es un comando o no.
         */
        public static boolean isCommand(@NotNull String message) {
            return message.startsWith("/");
        }
    }
}
