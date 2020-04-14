package game.ui.user;

import com.artemis.E;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class UserStatus extends Table {



    public UserStatus(E e) {
        add(new Bar(Bar.Kind.HP, e)).width(300).height(24).padLeft(-30f).left();
        row();
        add(new Bar(Bar.Kind.MANA, e)).width(300).height(24).padLeft(-10f).padTop(-5f).left();
        row();
        add(new Bar(Bar.Kind.ENERGY, e)).width(150).height(24).padLeft(-30f).padTop(-5f).left();
    }

}
