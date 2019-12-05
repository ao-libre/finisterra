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
import shared.network.interaction.AddItem;
import shared.network.inventory.InventoryUpdate;
import shared.network.notifications.EntityUpdate;
import shared.network.sound.SoundNotification;
import shared.objects.types.*;
import shared.objects.types.common.TreeObj;
import shared.util.MapHelper;
import shared.util.Messages;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class WorkSystem {

    private int userId;

    public WorkSystem(GUI gui) {
        AOAssetManager assetManager = AOGame.getGlobalAssetManager();
        GameNotificationProcessor gameNotificationProcessor = GameScreen.world.getSystem( GameNotificationProcessor.class );
        this.userId = GameScreen.getPlayer();
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
                //WorkKind workKind = weaponObj.getWorkKind();
                switch( weaponObj.getWorkKind() ) {
                    case CUT:
                        assert tile != null;
                        if(tile.getObjIndex() > 0) {
                            Obj targetobj = objectHandler.getObject( tile.getObjIndex() ).get();
                            if(targetobj.getType().equals( Type.TREE )) {
                                TreeObj treeObj = (TreeObj) targetobj;
                                gameNotificationProcessor.processNotification( new SoundNotification(13));
                                gameNotificationProcessor.processNotification( (EntityUpdate
                                        .EntityUpdateBuilder.of ( player.getNetwork().id )
                                        .withComponents ( new AttackAnimation () ).build() ) );
                                gui.getConsole().addInfo(assetManager.getMessages( Messages.EMPTY_MSG,
                                        "trabajando ....." ));
                                ThreadLocalRandom random = ThreadLocalRandom.current();
                                int woody = random.nextInt(0, 10);
                                if (woody > 6) {
                                    // 1008 arbol elfico
                                    if(targetobj.getId() == 1008) {
                                        //1006 leña elfica
                                        addResourse( 1006 );
                                    } else {
                                        //58 leña
                                        addResourse( 58 );
                                    }
                                }else {
                                    //136 ramitas
                                    addResourse( 136 );
                                }
                            } else {
                                gui.getConsole().addInfo( assetManager.getMessages( Messages.EMPTY_MSG,
                                        "el recurso no es el correcto" ));
                            }
                        } else {
                            //assetManager.getMessages( Messages.NO_WORKING_TOOOL_EQUIPED )
                            gui.getConsole().addInfo( assetManager.getMessages( Messages.EMPTY_MSG,
                                    "No hay Recursos frente a ti" ));
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
                                gui.getConsole().addInfo( assetManager.getMessages( Messages.EMPTY_MSG,
                                        "trabajando ..." ));
                                switch( targetobj.getName() ){
                                    case "Yacimiento de Hierro":
                                        addResourse( 192 );
                                        break;
                                    case "Yacimiento de Oro":
                                        addResourse( 193 );
                                        break;
                                    case "Yacimiento de Plata":
                                        addResourse( 194 );
                                        break;
                                }
                            } else {
                                gui.getConsole().addInfo( assetManager.getMessages( Messages.EMPTY_MSG,
                                        "el recurso no es el correcto" ));
                            }
                        } else {
                            //assetManager.getMessages( Messages.NO_WORKING_TOOOL_EQUIPED )
                            gui.getConsole().addInfo( assetManager.getMessages( Messages.EMPTY_MSG,
                                    "No hay Recursos frente a ti" ));
                        }
                        break;
                    case SAW:
                        //todo crear la UI y el funcionamiento

                        break;
                }
            } else {
                //assetManager.getMessages( Messages.NO_WORKING_TOOOL_EQUIPED )
                gui.getConsole().addInfo(assetManager.getMessages( Messages.EMPTY_MSG,
                        "NO TIENES EQUIPADA LA HERRAMIENTA NECESARIA" ));
            }
        } else {
            //assetManager.getMessages( Messages.NO_WORKING_TOOOL_EQUIPED )
            gui.getConsole().addInfo( assetManager.getMessages( Messages.EMPTY_MSG,
                    "NO TIENES EQUIPADA UNA HERRAMIENTA" ));
        }
    }
    private void addResourse(int objid){

        ObjectHandler objectHandler = GameScreen.world.getSystem( ObjectHandler.class );
        Obj obj = objectHandler.getObject( objid ).get();
        GameScreen.getClient().sendToAll(new AddItem( E.E(userId).getNetwork().id, obj ));
    }
}
