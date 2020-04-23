package game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class DialogText extends Table {

    private TextField textf;

    public DialogText() {
        setVisible(false);
    }

    public String getMessage() {
        return textf != null ? textf.getText() : "";
    }

    public void toggle() {
        if (textf == null) {
            textf = WidgetFactory.createTextField("");
        }
        setVisible(!isVisible());
        if (isVisible()) {
            add(textf).fillX();
            row().colspan(1).expandX().fillX();
            getStage().setKeyboardFocus(textf);
        } else {
            getStage().unfocus(textf);
            textf.setText("");
            removeActor(textf);
        }
    }

}
