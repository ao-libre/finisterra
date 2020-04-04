package game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Array;
import game.utils.Colors;
import game.utils.Skins;

public class AOConsole extends ScrollPane {

    private static final float MAX_MESSAGES = 100;
    private final Array<Label> messages = new Array<>(Label.class);

    public AOConsole() {
        super(createStack());
        setSmoothScrolling(true);
        setFadeScrollBars(true);
    }

    private static Actor createStack() {
        return new VerticalGroup();
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
        if (!messages.isEmpty() && messages.items.length >= MAX_MESSAGES) {
            messages.removeIndex(0);
            ((VerticalGroup) getActor()).removeActor(((VerticalGroup) getActor()).getChild(0), true);
        }
        messages.add(label);
        ((VerticalGroup) getActor()).addActor(label);
        layout();
        scrollTo(0, 0, 0, 0);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // @todo make it fancy, adding a background and user interaction
        super.draw(batch, parentAlpha * 0.8f);
    }
}
