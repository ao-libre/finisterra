package game.systems.network;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.utils.TimeUtils;
import com.esotericsoftware.minlog.Log;
import game.handlers.DefaultAOAssetManager;
import game.screens.CharacterSelectionScreen;
import game.screens.ScreenEnum;
import game.screens.ScreenManager;
import game.systems.physics.MovementProcessorSystem;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import org.jetbrains.annotations.NotNull;
import shared.network.account.AccountCreationResponse;
import shared.network.account.AccountLoginResponse;
import shared.network.interfaces.IResponseProcessor;
import shared.network.movement.MovementResponse;
import shared.network.time.TimeSyncResponse;
import shared.network.user.UserCreateResponse;
import shared.network.user.UserLoginResponse;
import shared.network.user.UserLogoutResponse;

@Wire
public class ClientResponseProcessor extends PassiveSystem implements IResponseProcessor {

    private ClientSystem clientSystem;
    private MovementProcessorSystem movementProcessorSystem;
    private ScreenManager screenManager;
    private TimeSync timeSync;
    private CharacterSelectionScreen characterSelectionScreen;
    @Wire
    private DefaultAOAssetManager assetManager;

    @Override
    public void processResponse(@NotNull MovementResponse movementResponse) {
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
    public void processResponse(@NotNull AccountCreationResponse accountCreationResponse) {
        if (accountCreationResponse.isSuccessful()) {
            screenManager.to(ScreenEnum.LOGIN);
            screenManager.showDialog("Aviso", "Cuenta creada con éxito!");
        } else {
            screenManager.showDialog("Error", "Error al crear la cuenta");
        }
    }

    @Override
    public void processResponse(@NotNull AccountLoginResponse accountLoginResponse) {
        if (accountLoginResponse.isSuccessful()) {
            characterSelectionScreen.setUserCharacters(accountLoginResponse.getCharacters());
            characterSelectionScreen.setUserCharactersData(accountLoginResponse.getCharactersData());
            characterSelectionScreen.setUserAcc(accountLoginResponse.getUsername());
            characterSelectionScreen.windowsUpdate();
            screenManager.to(ScreenEnum.CHAR_SELECT);
        } else {
            screenManager.showDialog("Error", assetManager.getMessages(accountLoginResponse.getError()));
        }
    }

    @Override
    public void processResponse(@NotNull UserCreateResponse userCreateResponse) {
        if (userCreateResponse.isSuccessful()) {
            screenManager.to(ScreenEnum.GAME);
        } else {
            // Mostramos un mensaje de error.
            screenManager.showDialog("Error", assetManager.getMessages(userCreateResponse.getMessage()));
        }
    }

    @Override
    public void processResponse(@NotNull UserLoginResponse userLoginResponse) {
        if (userLoginResponse.isSuccessful()) {
            screenManager.to(ScreenEnum.GAME);
        } else {
            screenManager.showDialog("Error", userLoginResponse.getMessage());
        }
    }
    @Override
    public void processResponse(@NotNull UserLogoutResponse userLogoutResponse) {
        if (userLogoutResponse.isSuccessful()) {
            clientSystem.disconnect();
        } else {
            screenManager.showDialog("Error", userLogoutResponse.getMessage());
        }
    }
}
