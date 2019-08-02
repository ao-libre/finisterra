package shared.network.notifications;

import shared.network.interfaces.INotification;
import shared.network.interfaces.INotificationProcessor;
import shared.util.Messages;

import static shared.network.notifications.ConsoleMessage.Kind.*;

public class ConsoleMessage implements INotification {

    private String message;
    private Messages messageId;
    private Kind kind;

    private ConsoleMessage(String message, Kind kind) {
        this.message = message;
        this.kind = kind;
    }

    private ConsoleMessage(Messages messageId, Kind kind) {
        this.messageId = messageId;
        this.kind = kind;
    }

    public static ConsoleMessage error(String message) {
        return new ConsoleMessage(message, ERROR);
    }

    public static ConsoleMessage info(Messages messageId) {
        return new ConsoleMessage(messageId, INFO);
    }

    public static ConsoleMessage infoCustom(String message) {
        return new ConsoleMessage(message, INFO);
    }

    public static ConsoleMessage combat(Messages message) {
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
