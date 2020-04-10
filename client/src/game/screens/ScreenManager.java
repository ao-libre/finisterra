package game.screens;

import com.badlogic.gdx.Screen;
import game.AOGame;
import net.mostlyoriginal.api.system.core.PassiveSystem;

public class ScreenManager extends PassiveSystem {
    private AOGame game;

    public ScreenManager(AOGame game) {
        this.game = game;
    }

    // Show in the game the screen which enum type is received
    public void to(ScreenEnum screen) {
        game.setScreen(screen.get());
    }

    public AbstractScreen getAbstractScreen() {
        return (AbstractScreen) getScreen();
    }

    public Screen getScreen() {
        return game.getScreen();
    }

}

