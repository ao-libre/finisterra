package game.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.TimeUtils;
import com.esotericsoftware.minlog.Log;
import game.AOGame;
import game.managers.WorldManager;
import game.screens.*;
import game.systems.network.ClientSystem;
import game.systems.network.TimeSync;
import game.systems.physics.MovementProcessorSystem;
import shared.network.interfaces.IResponseProcessor;
import shared.network.lobby.CreateRoomResponse;
import shared.network.lobby.JoinLobbyResponse;
import shared.network.lobby.JoinRoomResponse;
import shared.network.lobby.StartGameResponse;
import shared.network.lobby.player.PlayerLoginRequest;
import shared.network.movement.MovementResponse;
import shared.network.time.TimeSyncResponse;

public class ClientResponseProcessor implements IResponseProcessor {

    @Override
    public void processResponse(MovementResponse movementResponse) {
        MovementProcessorSystem.validateRequest(movementResponse.requestNumber, movementResponse.destination);
    }

    @Override
    public void processResponse(CreateRoomResponse createRoomResponse) {
        AOGame game = (AOGame) Gdx.app.getApplicationListener();
        LobbyScreen lobby = (LobbyScreen) game.getScreen();
        game.toRoom(lobby.getClientSystem(), createRoomResponse.getRoom(), createRoomResponse.getPlayer());
    }

    @Override
    public void processResponse(JoinLobbyResponse joinLobbyResponse) {
        AOGame game = (AOGame) Gdx.app.getApplicationListener();
        LoginScreen login = (LoginScreen) game.getScreen();
        game.toLobby(joinLobbyResponse.getPlayer(), joinLobbyResponse.getRooms(), login.getClientSystem());
    }

    @Override
    public void processResponse(JoinRoomResponse joinRoomResponse) {
        AOGame game = (AOGame) Gdx.app.getApplicationListener();
        LobbyScreen lobby = (LobbyScreen) game.getScreen();
        game.toRoom(lobby.getClientSystem(), joinRoomResponse.getRoom(), joinRoomResponse.getPlayer());
    }

    @Override
    public void processResponse(StartGameResponse startGameResponse) {
        AOGame game = (AOGame) Gdx.app.getApplicationListener();
        if (game.getScreen() instanceof RoomScreen) {
            RoomScreen roomScreen = (RoomScreen) game.getScreen();
            GameScreen gameScreen = (GameScreen) ScreenEnum.GAME.getScreen();
            ClientSystem clientSystem = new ClientSystem(startGameResponse.getHost(), startGameResponse.getTcpPort());
            clientSystem.start();
            gameScreen.initWorld(clientSystem);
            clientSystem.getKryonetClient().sendToAll(new PlayerLoginRequest(roomScreen.getPlayer()));
            Screen currentScreen = game.getScreen();
            game.setScreen(gameScreen);
            currentScreen.dispose();
        }
    }

    @Override
    public void processResponse(TimeSyncResponse timeSyncResponse) {
        TimeSync system = WorldManager.getWorld().getSystem(TimeSync.class);
        system.receive(timeSyncResponse);
        Log.info("Local timestamp: " + TimeUtils.millis() / 1000);
        Log.info("RTT: " + system.getRtt() / 1000);
        Log.info("Time offset: " + system.getTimeOffset() / 1000);
    }

}
