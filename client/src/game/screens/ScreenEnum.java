package game.screens;

import com.badlogic.gdx.Screen;

public enum ScreenEnum {
    LOADING(new LoadingScreen()),
    LOGIN(new LoginScreen()),
    SIGN_UP(new SignUpScreen()),
    LOBBY(new LobbyScreen()),
    ROOM(new RoomScreen()),
    GAME(new GameScreen());

    private Screen screen;

    ScreenEnum(Screen screen) {
        this.screen = screen;
    }

    public Screen get() {
        return screen;
    }
}