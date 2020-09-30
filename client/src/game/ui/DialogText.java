package game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class DialogText extends Table {

    private TextField textf;

    public DialogText() {
        setVisible(false);
        if (textf == null) {
            textf = WidgetFactory.createTextField("");
        }
        add(textf).growX();
    }

    public String getMessage() {
        return textf != null ? textf.getText() : "";
    }

    public void toggle() {

        setVisible(!isVisible());
        if (isVisible()) {
            getStage().setKeyboardFocus(textf);
        } else {
            getStage().unfocus(textf);
            textf.setText("");
        }
    }

}
