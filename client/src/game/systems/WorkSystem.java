package game.systems;

import com.artemis.E;
import com.esotericsoftware.minlog.Log;
import game.AOGame;
import game.handlers.AOAssetManager;
import game.handlers.ObjectHandler;
import game.screens.GameScreen;
import game.ui.GUI;
import game.utils.WorldUtils;
import position.WorldPos;
import shared.network.interaction.WorkRequest;
import shared.objects.types.Obj;
import shared.objects.types.WeaponKind;
import shared.objects.types.WeaponObj;
import shared.objects.types.WorkKind;
import shared.util.Messages;

import java.util.Optional;

public class WorkSystem {

    private int userId;

    public WorkSystem(GUI gui) {
        AOAssetManager assetManager = AOGame.getGlobalAssetManager();
        this.userId = GameScreen.getPlayer();
        E player = E.E( userId );
        ObjectHandler objectHandler = WorldUtils.getWorld().orElse( null )
                .getSystem( ObjectHandler.class );

        WorldPos worldPos = player.getWorldPos();

        if(player.hasWeapon()) {
            Log.info( "cheque1 cliente" );
            Optional< Obj > object = objectHandler.getObject( player.getWeapon().index );
            WeaponObj weaponObj = (WeaponObj) object.get();
            if(weaponObj.getKind().equals( WeaponKind.WORK )) {
                WorkKind workKind = weaponObj.getWorkKind();
                switch( workKind ) {
                    case FISHING:
                        //todo detectar el banco de peces pada poder empesar el desarrollo de esta parte
                        break;
                    case MINE:
                    case CUT:
                        GameScreen.getClient().sendToAll( new WorkRequest( workKind, worldPos ) );
                        break;
                    case SAW:
                        gui.getWorkUI().setWorkKind( WorkKind.SAW );
                        gui.getWorkUI().setVisible( !gui.getWorkUI().isVisible() );
                        break;
                    case FORGE:
                        gui.getWorkUI().setWorkKind( WorkKind.FORGE );
                        gui.getWorkUI().setVisible( !gui.getWorkUI().isVisible() );
                        break;
                }

            } else {
                gui.getConsole().addInfo( assetManager.getMessages( Messages.NO_WORK_TOOL ) );
            }
        } else {
            gui.getConsole().addInfo( assetManager.getMessages( Messages.NO_WORK_TOOL ) );
        }
    }
}
