package game.ui.user;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import game.AOGame;

public class UserInformation extends Table {

    public UserInformation() {
        UserImage head = new UserImage();
        UserStatus status = new UserStatus();

        add(head).left().prefHeight(getPrefSize()).prefWidth(getPrefSize());
        add(status).pad(-10).right();
        head.toFront();
    }

    private float getPrefSize() {
        float ratio = Gdx.graphics.getHeight() / AOGame.ORIGINAL_HEIGHT;
        return 64 * ratio;
    }
}