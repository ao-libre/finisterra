package game.ui.user;

import com.artemis.E;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class UserInformation extends Table {

    // TODO refactor. Don't use E
    public UserInformation(E e) {
        UserImage head = new UserImage(e);
        UserStatus status = new UserStatus(e);

        add(head).left().prefHeight(128).prefWidth(128);
        add(status).pad(-10).right();
        head.toFront();
    }
}
