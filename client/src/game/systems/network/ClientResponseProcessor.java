package game.systems.network;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.utils.TimeUtils;
import com.esotericsoftware.minlog.Log;
import game.screens.ScreenEnum;
import game.screens.ScreenManager;
import game.systems.physics.MovementProcessorSystem;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import shared.network.account.AccountCreationResponse;
import shared.network.account.AccountLoginResponse;
import shared.network.interfaces.IResponseProcessor;
import shared.network.movement.MovementResponse;
import shared.network.time.TimeSyncResponse;
import shared.network.user.UserCreateResponse;
import shared.network.user.UserLoginResponse;

@Wire
public class ClientResponseProcessor extends PassiveSystem implements IResponseProcessor {

    private ClientSystem clientSystem;
    private MovementProcessorSystem movementProcessorSystem;
    private ScreenManager screenManager;
    private TimeSync timeSync;

    @Override
    public void processResponse(MovementResponse movementResponse) {
        movementProcessorSystem.validateRequest(movementResponse.requestNumber, movementResponse.destination);
    }

    @Override
    public void processResponse(TimeSyncResponse timeSyncResponse) {
        timeSync.receive(timeSyncResponse);
        Log.debug("Local timestamp: " + TimeUtils.millis() / 1000);
        Log.debug("RTT: " + timeSync.getRtt() / 1000);
        Log.debug("Time offset: " + timeSync.getTimeOffset() / 1000);
    }

    @Override
    public void processResponse(AccountCreationResponse accountCreationResponse) {
        if (accountCreationResponse.isSuccessful()) {
            screenManager.to(ScreenEnum.LOGIN);
            Dialog dialog = new Dialog("Exito", screenManager.getAbstractScreen().getSkin());
            dialog.text("Cuenta creada con exito");
            dialog.button("OK");
            dialog.show(screenManager.getAbstractScreen().getStage()); //@todo crear dialogsystem
        }
        else {
            Dialog dialog = new Dialog("Error", screenManager.getAbstractScreen().getSkin());
            dialog.text("Error al crear la cuenta");
            dialog.button("OK");
            dialog.show(screenManager.getAbstractScreen().getStage());
        }
    }

    @Override
    public void processResponse(AccountLoginResponse accountLoginResponse) {
        if (accountLoginResponse.isSuccessful()) {
            /*
            Dialog dialog = new Dialog("Exito", screen.getSkin());
            dialog.text("Logueado con exito");
            dialog.button("OK");
            dialog.show(screen.getStage());
            */

            //hotfix para recuperar funcionalidad
            screenManager.to(ScreenEnum.CREATE);
        } else {
            Dialog dialog = new Dialog("Error", screenManager.getAbstractScreen().getSkin());
            dialog.text("Error al loguearse");
            dialog.button("OK");
            dialog.show(screenManager.getAbstractScreen().getStage());
        }
    }

    @Override
    public void processResponse(UserCreateResponse userCreateResponse) {
        if (userCreateResponse.isSuccessful()) {
            screenManager.to(ScreenEnum.GAME);
        } else {
            Dialog dialog = new Dialog("No se pudo crear el personaje!", screenManager.getAbstractScreen().getSkin());
            dialog.text(userCreateResponse.getMessage());
            dialog.button("OK");
            dialog.show(screenManager.getAbstractScreen().getStage());
        }
    }

    @Override
    public void processResponse(UserLoginResponse userLoginResponse) {
        if (userLoginResponse.isSuccessful()) {
            screenManager.to(ScreenEnum.GAME);
        } else {
            Dialog dialog = new Dialog("Error", screenManager.getAbstractScreen().getSkin());
            dialog.text(userLoginResponse.getMessage());
            dialog.button("OK");
            dialog.show(screenManager.getAbstractScreen().getStage());
        }
    }
}
