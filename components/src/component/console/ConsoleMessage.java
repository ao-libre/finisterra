package component.console;

import com.artemis.Component;

import static component.console.ConsoleMessage.Kind.*;


public class ConsoleMessage extends Component {

    private String messageId;
    private String[] messageParams;
    private Kind kind;

    public ConsoleMessage() {
    }

    public ConsoleMessage(String messageId, Kind kind, String... messageParams) {
        this.messageId = messageId;
        this.messageParams = messageParams;
        this.kind = kind;
    }

    public static ConsoleMessage error(String messageId, String... messageParams) {
        return new ConsoleMessage(messageId, ERROR, messageParams);
    }

    public static ConsoleMessage info(String messageId, String... messageParams) {
        return new ConsoleMessage(messageId, INFO, messageParams);
    }

    public static ConsoleMessage combat(String messageId, String... messageParams) {
        return new ConsoleMessage(messageId, COMBAT, messageParams);
    }

    public static ConsoleMessage warning(String messageId, String... messageParams) {
        return new ConsoleMessage(messageId, WARNING, messageParams);
    }

    public String getMessageId() {
        return messageId;
    }

    public String[] getMessageParams() {
        return messageParams;
    }

    public Kind getKind() {
        return kind;
    }


    public enum Kind {
        INFO,
        ERROR,
        WARNING,
        COMBAT
    }
}
