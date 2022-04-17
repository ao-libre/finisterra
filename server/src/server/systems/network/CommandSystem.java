package server.systems.network;

import com.artemis.ComponentMapper;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.minlog.Log;
import component.console.ConsoleMessage;
import component.entity.character.status.Health;
import component.entity.npc.OriginPos;
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

// @todo Esta clase podría ser estática, puede definirse en tiempo de compilación
public class CommandSystem extends PassiveSystem {

    private final Map<String, Consumer<Command>> commands = new HashMap<>();
    // Injected Systems
    private ServerSystem networkManager;
    private MapSystem mapSystem;
    private WorldEntitiesSystem worldEntitiesSystem;
    private MessageSystem messageSystem;

    ComponentMapper<WorldPos> mWorldPos;
    ComponentMapper<OriginPos> mOriginPos;
    ComponentMapper<Health> mHealth;

    /**
     * Aca se prepara una lista de comandos disponibles para usar desde el cliente.
     *
     * @see ServerRequestProcessor#processRequest(TalkRequest, int)
     */
    public CommandSystem() {
        commands.put("online", (command) -> {
            String connections = String.valueOf(networkManager.getAmountConnections());
            messageSystem.add(command.userId, ConsoleMessage.info(Messages.PLAYERS_ONLINE.name(), connections));
        });
        commands.put("salir", (command) -> {
            int connectionId = networkManager.getConnectionByPlayer(command.userId);
            networkManager.disconnected(connectionId);
        });
        commands.put("die", (command) -> worldEntitiesSystem.entityDie(command.userId));
        commands.put("sethome", (command) -> {
            int userId = command.userId;
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

            WorldPos worldPos = mWorldPos.get(userId);
            boolean homeSet = false;
            int i = 0;
            while ((i < capacity) && !homeSet) {
                if (worldPos.map == cityMaps.get(i)) {
                    OriginPos originPos = mOriginPos.create(userId);
                    originPos.map = worldPos.map;
                    originPos.x = worldPos.x;
                    originPos.y = worldPos.y;
                    messageSystem.add(userId, ConsoleMessage.info("HOME_SET"));
                    homeSet = true;
                }
                i++;
            }
            if (!homeSet) {
                messageSystem.add(userId, ConsoleMessage.info("ONLY_MAPS", "" + cityMaps));
            }
        });
        commands.put("seehome", (command) -> {
            OriginPos originPos = mOriginPos.create(command.userId);
            messageSystem.add(command.userId, ConsoleMessage.info("HOME_POS",
                    String.valueOf(originPos.map), String.valueOf(originPos.x), String.valueOf(originPos.y)));
        });
        commands.put("resurrect", (command) -> {
            int userId = command.userId;
            Health health = mHealth.get(userId);
            if (health.min == 0) {
                worldEntitiesSystem.resurrectRequest(userId);
                messageSystem.add(userId, ConsoleMessage.info("TIME_TO_RESURRECT"));
            } else {
                messageSystem.add(userId, ConsoleMessage.info("YOU_ARE_ALIVE"));
            }
        });
        commands.put("tp", (command) -> {
            int userId = command.userId;
            int map = Integer.parseInt(command.params[0]);
            int x = Integer.parseInt(command.params[1]);
            int y = Integer.parseInt(command.params[2]);

            WorldPos worldPos = mWorldPos.get(userId);

            if (mapSystem.getHelper().isValid(new WorldPos(x, y, map))) {
                worldPos.map = map;
                worldPos.x = x;
                worldPos.y = y;
                EntityUpdateBuilder resetUpdate = EntityUpdateBuilder.of(userId);
                resetUpdate.withComponents(worldPos);
                worldEntitiesSystem.notifyUpdate(userId, resetUpdate.build());
            }
        });
    }

    /**
     * Ejecuta el comando desde la lista {@link #commands}
     *
     * @param commandString String completo del comando.
     * @param userId Identificador del jugador.
     */
    public void handleCommand(@NotNull String commandString, int userId) {
        Command command = new Command(commandString, userId);
        try {
            commands.get(command.name).accept(command);
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
     * Se fija si el string pasado como parametro tiene
     * el prefijo caracteristico que poseen los comandos.
     *
     * @param message Mensaje a analizar.
     * @return boolean Si es un comando o no.
     */
    public static boolean isCommand(@NotNull String message) {
        return message.startsWith("/");
    }

    /**
     * Guardo el userId y parseo de el comando separando el nombre del mismo y sus argumentos.
     * Lo preparo para usarlo en {@link #handleCommand(String, int)}
     */
    public static class Command {
        // entityId del usuario
        public int userId;
        // nombre del comando
        public String name;
        // parámetros separados por ' ' o bien el string entero
        public String[] params;
        public String raw;

        @Contract(pure = true)
        public Command(@NotNull String message, int userId) {
            this.userId = userId;

            int endIndex = message.length();
            int sep = message.indexOf(" ");
            if (sep == -1) sep = endIndex;

            name = message.substring(1, sep);
            raw = message.substring(sep, endIndex);
            params = raw.split(" ");
        }
    }
}
