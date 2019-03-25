package game.ui.user;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class UserInformation extends Table {

    private UserImage head = new UserImage();
    private UserStatus status = new UserStatus();

    public UserInformation() {
        add(head).prefHeight(64).prefWidth(64).left().bottom().pad(10);
        add(status).right().bottom().pad(10);
    }

}
