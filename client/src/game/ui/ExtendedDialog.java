package game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/** Extensión que permite trabajar con expresiones lambda y runnables.
 * @see com.badlogic.gdx.scenes.scene2d.ui.Dialog
 * @todo Agregar más funcionalidad
 */
public class ExtendedDialog extends Dialog {
    public ExtendedDialog(String title, Skin skin) {
        super(title, skin);
    }

    public ExtendedDialog(String title, Skin skin, String windowStyleName) {
        super(title, skin, windowStyleName);
    }

    public ExtendedDialog(String title, WindowStyle windowStyle) {
        super(title, windowStyle);
    }

    public Dialog button (String text, Runnable runnable) {
        return super.button(text, runnable);
    }

    public Dialog button (String text, Runnable runnable, TextButton.TextButtonStyle buttonStyle) {
        return super.button(text, runnable, buttonStyle);
    }

    public Dialog button (Button button, Runnable runnable) {
        return super.button(button, runnable);
    }

    @Override
    protected void result(Object object) {
        if (object instanceof Runnable) {
            ((Runnable) object).run();
        }
    }
}
