package game.screens;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import game.AOGame;
import game.handlers.DefaultAOAssetManager;
import game.systems.resources.MusicSystem;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ScreenManager extends PassiveSystem {

    private final AOGame game;
    private final List<Consumer<ScreenEnum>> listeners;
    private MusicSystem musicSystem;
    @Wire
    private DefaultAOAssetManager assetManager;

    public ScreenManager(AOGame game) {
        this.game = game;
        listeners = new ArrayList<>();
    }

    public void addListener(Consumer<ScreenEnum> listener) {
        listeners.add(listener);
    }

    // Show in the game the screen which enum type is received
    public void to(@NotNull ScreenEnum screen) {
        game.setScreen(screen.get());
        listeners.forEach(listener -> listener.accept(screen));
    }

    public Screen getScreen() {
        return game.getScreen();
    }

    public AbstractScreen getAbstractScreen() {
        return (AbstractScreen) getScreen();
    }

    public void showDialog(String title, String message) {
        Dialog dialog = new Dialog(title, getAbstractScreen().getSkin());
        dialog.text(message);
        dialog.button("OK");
        dialog.show(getAbstractScreen().getStage());
    }
}

