package game.screens;

import com.badlogic.gdx.Screen;
import game.AOGame;
import game.systems.resources.MusicSystem;
import net.mostlyoriginal.api.system.core.PassiveSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ScreenManager extends PassiveSystem {
    private AOGame game;
    private MusicSystem musicSystem;
    private List<Consumer<ScreenEnum>> listeners;

    public ScreenManager(AOGame game) {
        this.game = game;
        listeners = new ArrayList<>();
    }

    public void addListener(Consumer<ScreenEnum> listener) {
        listeners.add(listener);
    }

    // Show in the game the screen which enum type is received
    public void to(ScreenEnum screen) {
        switch( screen ){
            case LOGIN:
                musicSystem.playMusic( 101 );
                musicSystem.fadeInMusic( 1f,20f );
                break;
            case GAME:
                musicSystem.playMusic( 1 );
                break;
        }
        game.setScreen(screen.get());
        listeners.forEach(listener -> listener.accept(screen));
    }

    public AbstractScreen getAbstractScreen() {
        return (AbstractScreen) getScreen();
    }

    public Screen getScreen() {
        return game.getScreen();
    }
}

