package game.screens;

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

    public AbstractScreen getScreen() {
        return (AbstractScreen) game.getScreen();
    }
}
