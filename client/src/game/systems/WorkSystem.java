package game.systems;

import com.artemis.E;
import com.badlogic.gdx.audio.Sound;
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
                                //TODO revisar si existen los objetos antes de agregarlos y que el inventario no este lleno
                                if (woody > 6) {
                                    if(targetobj.getId() == 1008) {
                                        gui.getConsole().addInfo( "Has obtenido 1 Leña Elfica" );
                                        player.getInventory().add( 1006, 1, false );
                                        gui.getInventory().updateUserInventory( 0 );
                                    } else {
                                        gui.getConsole().addInfo( "Has obtenido 1 Leña" );
                                        player.getInventory().add( 58, 1, false );
                                        gui.getInventory().updateUserInventory( 0 );
                                    }
                                }else {
                                    gui.getConsole().addInfo( "Has obtenido 1 ramita" );
                                    player.getInventory().add( 136, 1, false );
                                    gui.getInventory().updateUserInventory( 0 );
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
                                // todo agregar item metal al inventario
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
}
