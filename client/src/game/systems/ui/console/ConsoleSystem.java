package game.systems.ui.console;

import com.badlogic.gdx.scenes.scene2d.Actor;
import game.ui.AOConsole;
import net.mostlyoriginal.api.system.core.PassiveSystem;

public class ConsoleSystem extends PassiveSystem {

    public void addInfo(String messages) {
    }

    public Actor getActor() {
        return new AOConsole();
    }
}
