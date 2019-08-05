package shared.network.notifications;

import shared.network.interfaces.INotification;
import shared.network.interfaces.INotificationProcessor;
import shared.util.Messages;

import static shared.network.notifications.ConsoleMessage.Kind.*;

public class ConsoleMessage implements INotification {

    private Messages messageId;
    private String[] messageParams;
    private Kind kind;
	
	public ConsoleMessage() {}
	
    public ConsoleMessage(Messages messageId, Kind kind, String... messageParams) {
        this.messageId = messageId;
        this.messageParams = messageParams;
        this.kind = kind;
    }

    public static ConsoleMessage error(Messages messageId, String... messageParams) {
        return new ConsoleMessage(messageId, ERROR, messageParams);
    }

    public static ConsoleMessage info(Messages messageId, String... messageParams) {
        return new ConsoleMessage(messageId, INFO, messageParams);
    }

    public static ConsoleMessage combat(Messages messageId, String... messageParams) {
        return new ConsoleMessage(messageId, COMBAT, messageParams);
    }

    public static ConsoleMessage warning(Messages messageId, String... messageParams) {
        return new ConsoleMessage(messageId, WARNING, messageParams);
    }

    public Messages getMessageId() {
        return messageId;
    }

    public String[] getMessageParams() {
        return messageParams;
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
