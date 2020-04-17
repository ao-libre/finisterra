package game.screens;

import com.badlogic.gdx.Screen;

public enum ScreenEnum {
    LOGIN(new LoginScreen()),
    SIGN_UP(new SignUpScreen()),
    //CREATE(new CreateScreen()),
    CHAR_SELECT(new CharacterSelectionScreen()),
    GAME(new GameScreen());

    private Screen screen;

    ScreenEnum(Screen screen) {
        this.screen = screen;
    }

    public Screen get() {
        return screen;
    }
}