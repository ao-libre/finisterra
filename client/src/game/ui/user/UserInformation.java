package game.ui.user;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class UserInformation extends Table {

    public UserInformation() {

        UserImage head = new UserImage();
        UserStatus status = new UserStatus();

        add(head).left().prefHeight(128).prefWidth(128);
        add(status).pad(-10).right();
        head.toFront();
    }
}