package game.systems;

import com.artemis.E;
import entity.character.states.Heading;
import game.AOGame;
import game.handlers.*;
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
import shared.network.notifications.EntityUpdate;
import shared.network.sound.SoundNotification;
import shared.objects.types.*;
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

                switch( weaponObj.getWorkKind() ) {
                    case CUT:
                        assert tile != null;
                        if(tile.getObjIndex() > 0) {
                            Obj targetobj = objectHandler.getObject( tile.getObjIndex() ).get();
                            if(targetobj.getType().equals( Type.TREE )) {
                                //envia el sonido del hachazo
                                gameNotificationProcessor.processNotification( new SoundNotification(13));
                                // envia la animacion
                                gameNotificationProcessor.processNotification( (EntityUpdate
                                        .EntityUpdateBuilder.of ( player.getNetwork().id )
                                        .withComponents ( new AttackAnimation () ).build() ) );
                                gui.getConsole().addInfo(assetManager.getMessages( Messages.WORKING ));

                                ThreadLocalRandom random = ThreadLocalRandom.current();
                                int woody = random.nextInt(0, 10);

                                if (woody > 6) {
                                    // 1008 arbol elfico
                                    if(targetobj.getId() == 1008) {
                                        //1006 leña elfica
                                        addResource( 1006 );
                                    } else {
                                        //58 leña
                                        addResource( 58 );
                                    }
                                }else {
                                    //136 ramitas
                                    addResource( 136 );
                                }
                            } else {
                                gui.getConsole().addInfo( assetManager.getMessages( Messages.WRONG_RESOURCE ));
                            }
                        } else {
                            gui.getConsole().addInfo( assetManager.getMessages( Messages.NO_RESOURCE ));
                        }
                        break;
                    case FISHING:
                        //todo detectar el banco de peces pada poder empesar el desarrollo de esta parte
                        new SoundNotification(15);
                        break;
                    case FORGE:
                        gui.getWorkUI().setWorkKind( WorkKind.FORGE );
                        gui.getWorkUI().setVisible(!gui.getWorkUI().isVisible());
                        break;
                    case MINE:
                        if(tile.getObjIndex() > 0) {
                            Obj targetobj = objectHandler.getObject( tile.getObjIndex() ).get();
                            if(targetobj.getType().equals( Type.DEPOSIT )) {
                                // envia el sonido
                                gameNotificationProcessor.processNotification(new SoundNotification(15));
                                // envia la animacion
                                gameNotificationProcessor.processNotification( (EntityUpdate
                                        .EntityUpdateBuilder.of ( userId )
                                        .withComponents ( new AttackAnimation ( ))).build());

                                gui.getConsole().addInfo( assetManager.getMessages( Messages.WORKING ));
                                switch( targetobj.getName() ){
                                    case "Yacimiento de Hierro":
                                        addResource( 192 );
                                        break;
                                    case "Yacimiento de Oro":
                                        addResource( 193 );
                                        break;
                                    case "Yacimiento de Plata":
                                        addResource( 194 );
                                        break;
                                }
                            } else {
                                gui.getConsole().addInfo( assetManager.getMessages( Messages.WRONG_RESOURCE ));
                            }
                        } else {
                            gui.getConsole().addInfo( assetManager.getMessages( Messages.NO_RESOURCE));
                        }
                        break;
                    case SAW:
                        gui.getWorkUI().setWorkKind( WorkKind.SAW );
                        gui.getWorkUI().setVisible(!gui.getWorkUI().isVisible());
                        break;
                }
            } else {
                gui.getConsole().addInfo(assetManager.getMessages( Messages.WRONG_WORK_TOOL ));
            }
        } else {
            gui.getConsole().addInfo( assetManager.getMessages( Messages.NO_WORK_TOOL ));
        }
    }

    private void addResource(int objID){
        //agrega el item al inventario
        GameScreen.getClient().sendToAll(new AddItem( E.E(userId).getNetwork().id, objID, 1 ));
    }
}
