package game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import game.utils.Colors;
import game.utils.Skins;

import java.util.LinkedList;

public class AOConsole extends Actor {

    private static final int LINE_HEIGHT = 20;
    private static final float MAX_MESSAGES = 9;
    private LinkedList<Actor> messages = new LinkedList<>();

    AOConsole() {
        super();
    }

    public void addInfo(String message) {
        addMessage(message, Colors.GREY);
    }

    public void addError(String message) {
        addMessage(message, Colors.TRANSPARENT_RED);
    }

    public void addWarning(String message) {
        addMessage(message, Colors.YELLOW);
    }

    public void addCombat(String message) {
        addMessage(message, Colors.COMBAT);
    }

    private void addMessage(String message, Color color) {
        LabelStyle labelStyle = new LabelStyle(Skins.COMODORE_SKIN.getFont("big"), color);
        Label label = new Label(message, labelStyle);
        label.setX(getX());
        if (messages.size() >= MAX_MESSAGES) {
            messages.pollLast();
        }
        messages.offerFirst(label);
        setY();
    }

    private void setY() {
        float v = getY() - messages.size() * LINE_HEIGHT;
        for (int i = 0; i < messages.size(); i++) {
            messages.get(i).setY(v + i * LINE_HEIGHT);
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
