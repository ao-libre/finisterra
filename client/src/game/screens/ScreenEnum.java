package game.screens;

import com.badlogic.gdx.Screen;
import game.ClientConfiguration;
import game.handlers.AOAssetManager;
import game.systems.network.ClientSystem;
import shared.model.lobby.Player;
import shared.model.lobby.Room;

public enum ScreenEnum {
    LOADING {
        @Override
        public Screen getScreen(Object... params) {
            return new LoadingScreen();
        }
    },
    LOGIN {
        @Override
        public Screen getScreen(Object... params) {
            return new LoginScreen();
        }
    },
    SIGNUP {
        @Override
        public Screen getScreen(Object... params) {
            return new SignUpScreen();
        }
    },
    LOBBY {
        @Override
        public Screen getScreen(Object... params) {
            Player player = (Player) params[0];
            Room[] rooms = (Room[]) params[1];
            return new LobbyScreen(player, rooms);
        }
    },
    ROOM {
        @Override
        public Screen getScreen(Object... params) {
            return new RoomScreen((Room) params[1], (Player) params[2]);
        }
    },
    GAME {
        @Override
        public Screen getScreen(Object... params) {
            return new GameScreen((ClientConfiguration) params[0], (AOAssetManager) params[1]);
        }
    };


    public abstract Screen getScreen(Object... params);
}
