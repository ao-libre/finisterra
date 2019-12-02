package game.systems;

import com.artemis.E;
import com.esotericsoftware.minlog.Log;
import entity.character.info.Inventory;
import entity.character.states.Heading;
import game.AOGame;
import game.handlers.*;
import game.managers.WorldManager;
import game.network.GameNotificationProcessor;
import game.screens.GameScreen;
import game.ui.GUI;
import game.utils.WorldUtils;
import physics.AttackAnimation;
import position.WorldPos;
import shared.interfaces.Constants;
import shared.model.map.Map;
import shared.model.map.Tile;
import shared.network.inventory.InventoryUpdate;
import shared.network.notifications.EntityUpdate;
import shared.network.sound.SoundNotification;
import shared.objects.types.*;
import shared.objects.types.common.TreeObj;
import shared.util.MapHelper;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class WorkSystem {

    private final GUI gui;
    private final AOAssetManager assetManager;
    private WorldManager worldManager;
    private GameNotificationProcessor gameNotificationProcessor;
    private ObjectHandler objectHandler;

    public WorkSystem(GUI gui) {
        this.gui = gui;
        this.assetManager = AOGame.getGlobalAssetManager();
        gameNotificationProcessor = GameScreen.world.getSystem( GameNotificationProcessor.class );
        int userId = GameScreen.getPlayer();
        E player = E.E( userId );
        ObjectHandler objectHandler = WorldUtils.getWorld().orElse( null )
                .getSystem( ObjectHandler.class );
        WorldPos worldPos = player.getWorldPos();
        Heading heading = player.getHeading();
        // si mal no entendi esto deveria devolver la posision enfrente el jugador
        // no me pregunten como funciona XDDDDDD
        WorldPos targetPos = new WorldPos(
                (heading.current == Constants.Heading.EAST.toInt() ? 1 : heading.current == Constants.Heading.WEST.toInt() ? -1 : 0) + worldPos.x,
                (heading.current == Constants.Heading.NORTH.toInt() ? -1 : heading.current == Constants.Heading.SOUTH.toInt() ? 1 : 0) + worldPos.y,
                worldPos.map );

        Map map = MapHandler.get( worldPos.getMap() );
        Tile tile = MapHelper.getTile( map, targetPos );

        if(player.hasWeapon()) {
            final Optional< Obj > object = objectHandler.getObject( player.getWeapon().index );
            WeaponObj weaponObj = (WeaponObj) object.get();
            if(weaponObj.getKind().equals( WeaponKind.WORK )) {
                WorkKind workKind = weaponObj.getWorkKind();
                switch( workKind ) {
                    case CUT:
                        assert tile != null;
                        if(tile.getObjIndex() > 0) {
                            Obj targetobj = objectHandler.getObject( tile.getObjIndex() ).get();
                            if(targetobj.getType().equals( Type.TREE )) {
                                TreeObj treeObj = (TreeObj) targetobj;
                                gameNotificationProcessor.processNotification( new SoundNotification(13));
                                //TODO ver porque no realiza la animacion de ataque
                                gameNotificationProcessor.processNotification( (EntityUpdate
                                        .EntityUpdateBuilder.of ( userId )
                                        .withComponents ( new AttackAnimation () ).build() ) );
                                gui.getConsole().addInfo( "trabajando ....." );
                                ThreadLocalRandom random = ThreadLocalRandom.current();
                                int woody = random.nextInt(0, 10);
                                if (woody > 6) {
                                    if(targetobj.getId() == 1008) {
                                        addResourse( 1006, player );
                                    } else {
                                        addResourse( 58, player );
                                    }
                                }else {
                                    addResourse( 136, player );
                                }
                            } else {
                                gui.getConsole().addInfo( "el recurso no es el correcto" );
                            }
                        } else {
                            //assetManager.getMessages( Messages.NO_WORKING_TOOOL_EQUIPED )
                            gui.getConsole().addInfo( "No hay Recursos frente a ti" );
                        }
                        break;
                    case FISHING:
                        //todo detectar el banco de peces pada poder empesar el desarrollo de esta parte
                        new SoundNotification(15);
                        break;
                    case FORGE:
                        //todo crear la UI y el funcionamiento
                        break;
                    case MINE:
                        if(tile.getObjIndex() > 0) {
                            Obj targetobj = objectHandler.getObject( tile.getObjIndex() ).get();
                            if(targetobj.getType().equals( Type.DEPOSIT )) {
                                gameNotificationProcessor.processNotification(new SoundNotification(15));
                                gameNotificationProcessor.processNotification( (EntityUpdate
                                        .EntityUpdateBuilder.of ( userId )
                                        .withComponents ( new AttackAnimation ( ))).build());
                                gui.getConsole().addInfo( "trabajando ....." );
                                String objname = targetobj.getName();
                                switch( objname ){
                                    case "Yacimiento de Hierro":
                                        addResourse( 192, player );
                                        break;
                                    case "Yacimiento de Oro":
                                        addResourse( 193, player );
                                        break;
                                    case "Yacimiento de Plata":
                                        addResourse( 194,player );
                                        break;
                                }
                            } else {
                                gui.getConsole().addInfo( "el recurso no es el correcto" );
                            }
                        } else {
                            //assetManager.getMessages( Messages.NO_WORKING_TOOOL_EQUIPED )
                            gui.getConsole().addInfo( "No hay Recursos frente a ti" );
                        }
                        break;
                    case SAW:
                        //todo crear la UI y el funcionamiento
                        break;
                }
            } else {
                //assetManager.getMessages( Messages.NO_WORKING_TOOOL_EQUIPED )
                gui.getConsole().addInfo( "NO TIENES EQUIPADA LA HERRAMIENTA NECESARIA" );
            }
        } else {
            //assetManager.getMessages( Messages.NO_WORKING_TOOOL_EQUIPED )
            gui.getConsole().addInfo( "NO TIENES EQUIPADA UNA HERRAMIENTA" );
        }
    }
    private void addResourse(int objid, E player){

        objectHandler = GameScreen.world.getSystem( ObjectHandler.class );
        Inventory.Item[] inventory = player.getInventory().items;
        int index = -1;
        int fistEmptySlot = -1;

        for (int i = 0; i<20 ;i++) {
            if(inventory[i] != null) {
                if(inventory[i].objId == objid) {
                    index = i;
                }else if(inventory[i].objId == 0){
                    if (fistEmptySlot == -1) {
                        fistEmptySlot = i;
                    }
                }
            } else {
                if (fistEmptySlot == -1) {
                    fistEmptySlot = i;
                }
            }
        }
        if (index != -1){
            inventory[index].count++;
            gui.getInventory().updateUserInventory( 0 );
        }else if ( fistEmptySlot != -1){
            player.getInventory().add( objid,1,false );
            gui.getInventory().updateUserInventory( 0 );
        }else {
            gui.getConsole().addInfo( "Inventario lleno" );
        }
    }
}
