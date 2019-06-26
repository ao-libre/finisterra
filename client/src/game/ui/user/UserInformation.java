package game.ui.user;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class UserInformation extends Table {

    private UserImage head = new UserImage();
    private UserStatus status = new UserStatus();

    public UserInformation() {
        add(head).left().prefHeight(128).prefWidth(128);
        add(status).pad(-10).right();
        head.toFront();
    }

}
