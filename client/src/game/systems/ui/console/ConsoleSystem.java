package game.systems.ui.console;

import com.artemis.Aspect;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.esotericsoftware.minlog.Log;
import component.console.ConsoleMessage;
import game.systems.resources.MessageSystem;
import game.ui.AOConsole;
import shared.util.Messages;

import static com.artemis.E.E;

@Wire
public class ConsoleSystem extends IteratingSystem {

    private MessageSystem messageSystem;
    private AOConsole console;

    public ConsoleSystem() {
        super(Aspect.all(ConsoleMessage.class));
        console = new AOConsole();
    }

    @Override
    protected void process(int entityId) {
        ConsoleMessage consoleMessage = E(entityId).getConsoleMessage();
        String message = messageSystem.getMessage(Messages.valueOf(consoleMessage.getMessageId()), consoleMessage.getMessageParams());
        switch (consoleMessage.getKind()) {
            case INFO:
                console.addInfo(message);
                break;
            case ERROR:
                console.addError(message);
                break;
            case COMBAT:
                console.addCombat(message);
                break;
            case WARNING:
                console.addWarning(message);
                break;
        }
        Log.debug("Delete from world: " + entityId);
        E(entityId).clear();
    }

    public AOConsole getConsole() {
        return console;
    }

}
