package game.systems;

import com.artemis.E;
import entity.character.states.Heading;
import game.AOGame;
import game.handlers.AOAssetManager;
import game.handlers.MapHandler;
import game.handlers.ObjectHandler;
import game.screens.GameScreen;
import game.ui.GUI;
import game.utils.WorldUtils;
import position.WorldPos;
import shared.interfaces.Constants;
import shared.model.map.Map;
import shared.model.map.Tile;
import shared.objects.types.*;
import shared.util.MapHelper;

import java.util.Optional;

public class WorkSystem {
    private final GUI gui;
    private final AOAssetManager assetManager;
    public WorkSystem(GUI gui) {
        this.gui = gui;
        this.assetManager = AOGame.getGlobalAssetManager();

        E player = E.E( GameScreen.getPlayer() );
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
                                gui.getConsole().addInfo( "trabajando ....." );
                                // todo agregar item madera al inventario
                            } else {
                                gui.getConsole().addInfo( "el recurso no es el correcto" );
                            }
                        } else {
                            //assetManager.getMessages( Messages.NO_WORKING_TOOOL_EQUIPED )
                            gui.getConsole().addInfo( "No hay Recursos frente a ti" );
                        }
                        break;
                    case FISHING:
                        break;
                    case FORGE:
                        break;
                    case MINE:
                        if(tile.getObjIndex() > 0) {
                            Obj targetobj = objectHandler.getObject( tile.getObjIndex() ).get();
                            if(targetobj.getType().equals( Type.DEPOSIT )) {
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
