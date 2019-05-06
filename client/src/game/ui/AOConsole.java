package game.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import game.utils.Colors;
import game.utils.Fonts;

import java.util.LinkedList;

public class AOConsole extends Actor {

    static final float MAX_MESSAGES = 6;
    private static Label.LabelStyle ERROR_STYLE = new Label.LabelStyle(Fonts.CONSOLE_FONT, Colors.TRANSPARENT_RED);
    private static Label.LabelStyle INFO_STYLE = new Label.LabelStyle(Fonts.CONSOLE_FONT, Colors.GREY);
    private static Label.LabelStyle WARNING_STYLE = new Label.LabelStyle(Fonts.CONSOLE_FONT, Colors.YELLOW);
    private static Label.LabelStyle COMBAT_STYLE = new Label.LabelStyle(Fonts.CONSOLE_FONT, Colors.COMBAT);
    private LinkedList<Actor> messages = new LinkedList<>();

    AOConsole() {
        super();
    }

    public void addInfo(String message) {
        addMessage(message, INFO_STYLE);
    }

    public void addError(String message) {
        addMessage(message, ERROR_STYLE);
    }

    public void addWarning(String message) {
        addMessage(message, WARNING_STYLE);
    }

    public void addCombat(String message) {
        addMessage(message, COMBAT_STYLE);
    }

    private void addMessage(String message, Label.LabelStyle style) {
        Label label = new Label(message, style);
        label.setX(getX());
        if (messages.size() >= MAX_MESSAGES) {
            messages.pollLast();
        }
        messages.offerFirst(label);
        setY();
    }

    private void setY() {
        for (int i = 0; i < messages.size(); i++) {
            messages.get(i).setY(getY() + i * 16);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (messages.isEmpty()) {
            return;
        }
        // draw background
        messages.forEach(message -> {
            float index = MAX_MESSAGES - messages.indexOf(message);
            float alpha = index / MAX_MESSAGES;
            message.draw(batch, parentAlpha * alpha);
        });
    }
}
