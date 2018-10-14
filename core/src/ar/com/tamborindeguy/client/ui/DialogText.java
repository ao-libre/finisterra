package ar.com.tamborindeguy.client.ui;

import ar.com.tamborindeguy.client.utils.Skins;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class DialogText extends Table {

    private final TextField textf;

    DialogText() {
        textf = new TextField("", Skins.COMODORE_SKIN, "transparent");
        row().colspan(1).expandX().fillX();
        add(textf).fillX();
        setVisible(false);
    }

    public String getMessage() {
        return textf.getText();
    }

    public void toggle() {
        setVisible(!isVisible());
        if (isVisible()) {
            getStage().setKeyboardFocus(textf);
        }
    }
}
