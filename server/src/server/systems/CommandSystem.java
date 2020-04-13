package server.systems;

import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.utils.Array;
import component.console.ConsoleMessage;
import server.systems.manager.DefaultManager;
import server.systems.manager.WorldManager;
import server.systems.network.MessageSystem;
import shared.util.Messages;

@Wire
public class CommandSystem extends DefaultManager {

    private static final String CMD_PLAYERS_COUNT = "/playersonline";
    private static final String CMD_PLAYER_SET_HOME_CITY = "/sethome";
    private static final String CMD_PLAYER_SEE_HOME_CITY = "/seehome";
    private static final String CMD_PLAYER_RESURRECT = "/resurrect";
    private static final String CMD_PLAYER_DIE = "/die";
    // Injected Systems
    private ServerSystem networkManager;
    private WorldManager worldManager;
    private MessageSystem messageSystem;

    public void handleCommand(String command, int senderId) {
        // Example command to get the online players
        if (command.equalsIgnoreCase(CMD_PLAYERS_COUNT)) {
            String connections = String.valueOf(networkManager.getAmountConnections());
            messageSystem.add(senderId, ConsoleMessage.info(Messages.PLAYERS_ONLINE.name(), connections));
        }

        if (command.equalsIgnoreCase( CMD_PLAYER_SET_HOME_CITY )){
            final int capacity = 17;
            Array<Integer> cityMaps = new Array(capacity);

            cityMaps.add( CityMapsNumbers.ABADIA_LINDOS );
            cityMaps.add( CityMapsNumbers.AFUERAS_BANDERBILL );
            cityMaps.add( CityMapsNumbers.ARGHAL_OESTE );
            cityMaps.add( CityMapsNumbers.CENTRO_ARGHAL );
            cityMaps.add( CityMapsNumbers.CENTRO_BANDERBILL );
            cityMaps.add( CityMapsNumbers.CENTRO_LINDOS );
            cityMaps.add( CityMapsNumbers.CIUDAD_ARKHEIN );
            cityMaps.add( CityMapsNumbers.CIUDAD_BANDERBILL );
            cityMaps.add( CityMapsNumbers.CIUDAD_LINDOS );
            cityMaps.add( CityMapsNumbers.CIUDAD_NUEVA_ESPERANZA );
            cityMaps.add( CityMapsNumbers.MUELLES_ARGHAL );
            cityMaps.add( CityMapsNumbers.MUELLES_BANDERBILL );
            cityMaps.add( CityMapsNumbers.NEMAHUAK );
            cityMaps.add( CityMapsNumbers.NIX );
            cityMaps.add( CityMapsNumbers.PUENTES_ARKHEIN );
            cityMaps.add( CityMapsNumbers.PUERTO_ARKHEIN );
            cityMaps.add( CityMapsNumbers.ULLATHORPE );

            E player = E.E(senderId);
            int playerMap = player.worldPosMap(), playerX = player.worldPosX(), playerY = player.worldPosY();
            boolean homeSet = false;
            int i = 0;
            while ((i < (capacity - 1)) || homeSet) {
                if (playerMap == cityMaps.get( i )){
                    player.originPosMap( playerMap ).originPosX( playerX ).originPosY( playerY );
                    messageSystem.add(senderId, ConsoleMessage.info("HOME_SET"));
                    homeSet = true;
                }
                i++;
            }
            if (!homeSet){
                messageSystem.add(senderId, ConsoleMessage.info("ONLY_MAPS", "" +cityMaps));
            }
        }

        if (command.equalsIgnoreCase( CMD_PLAYER_SEE_HOME_CITY )){
            E player = E.E(senderId);
            messageSystem.add(senderId, ConsoleMessage.info( "HOME_POS", String.valueOf(player.originPosMap()), String.valueOf(player.originPosX()), String.valueOf(player.originPosY())));
        }

        if (command.equalsIgnoreCase( CMD_PLAYER_RESURRECT )){
            E player = E.E(senderId);
            if (player.healthMin() == 0) {
                worldManager.resurrectRequest( senderId );
                messageSystem.add( senderId, ConsoleMessage.info("TIME_TO_RESURRECT"));
            } else{
                messageSystem.add( senderId, ConsoleMessage.info("YOU_ARE_ALIVE"));
            }
        }

        if (command.equalsIgnoreCase( CMD_PLAYER_DIE )){
            worldManager.entityDie( senderId );
        }

    }
}
