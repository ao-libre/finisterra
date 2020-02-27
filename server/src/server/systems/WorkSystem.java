package server.systems;

import com.artemis.BaseSystem;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.esotericsoftware.minlog.Log;
import entity.character.info.Inventory;
import entity.character.states.Heading;
import physics.AttackAnimation;
import position.WorldPos;
import server.network.ServerNotificationProcessor;
import server.systems.manager.MapManager;
import server.systems.manager.ObjectManager;
import server.systems.manager.WorldManager;
import shared.interfaces.Constants;
import shared.model.map.Map;
import shared.model.map.Tile;
import shared.network.interaction.AddItem;
import shared.network.interaction.WorkRequest;
import shared.network.inventory.ForgeRecipes;
import shared.network.inventory.SawRecipes;
import shared.network.notifications.ConsoleMessage;
import shared.network.notifications.EntityUpdate;
import shared.network.sound.SoundNotification;
import shared.objects.types.Obj;
import shared.objects.types.Type;
import shared.objects.types.WorkKind;
import shared.util.MapHelper;
import shared.util.Messages;

import java.util.concurrent.ThreadLocalRandom;

@Wire
public class WorkSystem extends BaseSystem {

    private WorldManager worldManager;
    private int userId;
    private int needObjID = 0, needCount = 0, needCount2 = 0, needObjID2 = 0, needCount3 = 0, needObjID3 = 0, resultObjID = 0 , resultCount = 0;

    // codigo tomado y adaptado del original que estaba en el cliente
    public void works(int userId, WorkRequest workRequest){
       // this.workRequest = workRequest;
        Log.info( "worksystem flag 1" + userId +" ---- "+ workRequest);
        WorkKind workKind = workRequest.getWorkKind();
        this.userId = userId;


        E player = E.E( userId );
        WorldPos worldPos = player.getWorldPos();
        Heading heading = player.getHeading();
        // si mal no entendi esto deveria devolver la posision enfrente el jugador
        WorldPos targetPos = new WorldPos(
                (heading.current == Constants.Heading.EAST.toInt() ? 1 : heading.current == Constants.Heading.WEST.toInt() ? -1 : 0) + worldPos.x,
                (heading.current == Constants.Heading.NORTH.toInt() ? -1 : heading.current == Constants.Heading.SOUTH.toInt() ? 1 : 0) + worldPos.y,
                worldPos.map );

        MapManager mapManager = world.getSystem( MapManager.class );
        MapHelper mapHelper = mapManager.getHelper();
        Map map = mapHelper.getMap( worldPos.map );
        Tile tile = MapHelper.getTile( map , targetPos );

        ObjectManager objectHandler = world.getSystem( ObjectManager.class );

            switch( workKind ) {

                case CUT:
                    Log.info( "cut" );
                    assert tile != null;
                    if(tile.getObjIndex() > 0) {
                        Obj targetobj = objectHandler.getObject( tile.getObjIndex() ).get();
                        if(targetobj.getType().equals( Type.TREE )) {
                            //envia el sonido del hachazo
                            worldManager.notifyUpdate(userId, new SoundNotification(13));
                            // envia la animacion
                            worldManager.notifyUpdate ( userId, EntityUpdate.EntityUpdateBuilder.of ( userId ).withComponents ( new AttackAnimation( ) ).build ( ) );
                            // envia el mensaje trabajando
                            consoleMessage( userId, Messages.WORKING );

                            ThreadLocalRandom random = ThreadLocalRandom.current();
                            int woody = random.nextInt(0, 10);

                            if (woody > 6) {
                                // 1008 arbol elfico
                                if(targetobj.getId() == 1008) {
                                    //1006 leña elfica
                                    addResource( 1006,1 );
                                } else {
                                    //58 leña
                                    addResource( 58,1 );
                                }
                            }else {
                                //136 ramitas
                                addResource( 136,1 );
                            }
                        } else {
                            consoleMessage( userId, Messages.WRONG_RESOURCE );
                        }
                    } else {
                        consoleMessage( userId, Messages.NO_RESOURCE );
                    }
                    break;
                case FISHING:
                    //todo detectar el banco de peces pada poder empesar el desarrollo de esta parte
                    new SoundNotification(15);
                    break;

                case MINE:
                    if(tile.getObjIndex() > 0) {
                        Obj targetobj = objectHandler.getObject( tile.getObjIndex() ).get();
                        if(targetobj.getType().equals( Type.DEPOSIT )) {
                            // envia el sonido
                            worldManager.notifyUpdate(userId, new SoundNotification(15));
                            // envia la animacion
                            worldManager.notifyUpdate(userId, (EntityUpdate
                                    .EntityUpdateBuilder.of ( userId )
                                    .withComponents ( new AttackAnimation ( ))).build());

                            consoleMessage( userId, Messages.WORKING );
                            switch( targetobj.getName() ){
                                case "Yacimiento de Hierro":
                                    addResource( 192, 1 );
                                    break;
                                case "Yacimiento de Oro":
                                    addResource( 193,1 );
                                    break;
                                case "Yacimiento de Plata":
                                    addResource( 194,1 );
                                    break;
                            }
                        } else {
                            consoleMessage( userId, Messages.WRONG_RESOURCE );
                        }
                    } else {
                        consoleMessage( userId, Messages.NO_RESOURCE);
                    }
                    break;

            }
    }
    public void craft(int userId, WorkRequest workRequest){
        WorkKind workKind = workRequest.getWorkKind();
        this.userId = userId;
        String recipeName = workRequest.getRecipeName();
        switch( workKind ) {
            case SAW:
                SawRecipes sawRecipes = SawRecipes.valueOf( recipeName );
                needCount = sawRecipes.getNeedCount();
                needObjID = sawRecipes.getNeedObjID();
                needCount2 = sawRecipes.getNeedCount2();
                needObjID2 = sawRecipes.getNeedObjID2();
                needCount3 = sawRecipes.getNeedCount3();
                needObjID3 = sawRecipes.getNeedObjID3();
                //obtiene resultado
                resultCount = sawRecipes.getResultCount();
                resultObjID = sawRecipes.getResultObjID();
                if(hasRequestItems( needCount, needObjID, needCount2, needObjID2, needCount3, needObjID3 )) {
                    //remueve los items necesarios

                    addResource( needObjID, -needCount );
                    if(needObjID2 > 0) {
                        addResource( needObjID2, -needCount2 );
                    }
                    if(needObjID3 > 0) {
                        addResource( needObjID3, -needCount3 );
                    }
                    //agrega el resultado de la creacion
                    addResource( resultObjID, resultCount );
                } else {
                    consoleMessage( userId, Messages.NOT_HAVE_NECESSARY_RESOURCE );
                }
                break;
            case FORGE:
                ForgeRecipes forgeRecipes = ForgeRecipes.valueOf( recipeName );
                needCount = forgeRecipes.getNeedCount();
                needObjID = forgeRecipes.getNeedObjID();
                needCount2 = forgeRecipes.getNeedCount2();
                needObjID2 = forgeRecipes.getNeedObjID2();
                needCount3 = forgeRecipes.getNeedCount3();
                needObjID3 = forgeRecipes.getNeedObjID3();
                //obtiene resultado
                resultCount = forgeRecipes.getResultCount();
                resultObjID = forgeRecipes.getResultObjID();

                if(hasRequestItems( needCount, needObjID, needCount2, needObjID2, needCount3, needObjID3 )) {
                    //remueve los items necesarios

                    addResource( needObjID, -needCount );
                    if(needObjID2 > 0) {
                        addResource( needObjID2, -needCount2 );
                    }
                    if(needObjID3 > 0) {
                        addResource( needObjID3, -needCount3 );
                    }
                    //agrega el resultado de la creacion
                    addResource( resultObjID, resultCount );
                } else {
                    consoleMessage( userId, Messages.NOT_HAVE_NECESSARY_RESOURCE );
                }
                break;
        }
    }




    private void addResource(int objID, int count){
        //agrega el item al inventario
        ServerNotificationProcessor serverNotificationProcessor = world.getSystem( ServerNotificationProcessor.class );
        AddItem addItem = new AddItem( userId, objID, count );
        serverNotificationProcessor.processNotification( addItem );
    }
    private void consoleMessage(int userId, Messages messages ) {
        ConsoleMessage combat = ConsoleMessage.combat( messages );
        worldManager.sendEntityUpdate( userId, combat );
    }

    @Override
    protected void processSystem() {

    }

    //chequea que poseas los items necesarios para la creacion
    private boolean hasRequestItems(int needCount, int needObjID, int needCount2, int needObjID2,int needCount3, int needObjID3){
        E player = E.E (userId);
        Inventory.Item[] items = player.getInventory().items;
        boolean need1=false, need2=false, need3=false;

        if (needObjID2 == 0){
            need2 = true;
        }
        if (needObjID3 == 0){
            need3 = true;
        }

        for (int i = 0; i <20 ; i++) {
            if (items[i] != null){
                if (!need1) {
                    if(items[i].objId == needObjID) {
                        if(items[i].count >= needCount) {
                            need1 = true;
                        }
                    }
                }
                if (!need2) {
                    if(items[i].objId == needObjID2) {
                        if(items[i].count >= needCount2) {
                            need2 = true;
                        }
                    }
                }
                if(!need3) {
                    if(items[i].objId == needObjID3) {
                        if(items[i].count >= needCount3) {
                            need3 = true;
                        }
                    }
                }
            }

        }

        return need1 && need2 && need3;
    }
}