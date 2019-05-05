package shared.network.notifications;

import shared.network.interfaces.INotification;
import shared.network.interfaces.INotificationProcessor;

import static shared.network.notifications.ConsoleMessage.Kind.*;

public class ConsoleMessage implements INotification {

    private String message;
    private Kind kind;

    public ConsoleMessage() {
    }

    private ConsoleMessage(String message, Kind kind) {
        this.message = message;
        this.kind = kind;
    }

    public static ConsoleMessage error(String message) {
        return new ConsoleMessage(message, ERROR);
    }

    public static ConsoleMessage info(String message) {
        return new ConsoleMessage(message, INFO);
    }

    public static ConsoleMessage combat(String message) {
        return new ConsoleMessage(message, COMBAT);
    }

    public static ConsoleMessage warning(String message) {
        return new ConsoleMessage(message, WARNING);
    }

    public String getMessage() {
        return message;
    }

    public Kind getKind() {
        return kind;
    }

    @Override
    public void accept(INotificationProcessor processor) {
        processor.processNotification(this);
    }

    public enum Kind {
        INFO,
        ERROR,
        WARNING,
        COMBAT
    }
}
