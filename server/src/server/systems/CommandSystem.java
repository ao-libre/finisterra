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
            /*
            Mapas:
            1: "Ullathorpe (Zona segura)"
            34: "Nix (Zona segura)"
            61: "Muelles de Banderbill (Zona segura)"
            60: "Centro de Banderbill (Zona segura)"
            59: "Ciudad de Banderbill: Muralla (Zona segura)"
            58: "Afueras de Banderbill (Zona segura)"
            62: "Centro de Lindos (Zona segura)"
            63: "Abadía de Lindos (Zona segura)"
            64: "Ciudad de Lindos (Zona segura)"
            156: "Puentes de Arkhein (Zona segura)"
            151: "Ciudad de Arkhein (Zona segura)"
            150: "Puerto de Arkhein (Zona segura)"
            195: "Arghal Oeste (Zona segura)"
            196: "Centro de Arghal (Zona segura)"
            197: "Muelles de Arghal (Zona segura)"
            112: "Ciudad de Nueva Esperanza (Zona segura)"
            286: "Nemahuak (Zona segura)"
             */
            cityMaps.add( 1 );
            cityMaps.add( 34 );
            cityMaps.add( 61 );
            cityMaps.add( 60 );
            cityMaps.add( 59 );
            cityMaps.add( 58 );
            cityMaps.add( 62 );
            cityMaps.add( 63 );
            cityMaps.add( 64 );
            cityMaps.add( 56 );
            cityMaps.add( 151 );
            cityMaps.add( 150 );
            cityMaps.add( 195 );
            cityMaps.add( 196 );
            cityMaps.add( 197 );
            cityMaps.add( 286 );
            cityMaps.add( 112 );

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
