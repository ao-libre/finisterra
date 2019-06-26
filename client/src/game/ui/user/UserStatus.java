package game.ui.user;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class UserStatus extends Table {

    private final static Bar hp = new Bar(Bar.Kind.HP);
    private final static Bar mana = new Bar(Bar.Kind.MANA);
    private final static Bar energy = new Bar(Bar.Kind.ENERGY);


    public UserStatus() {
        add(hp).width(300).height(24).padLeft(-30f).left();
        row();
        add(mana).width(300).height(24).padLeft(-10f).padTop(-5f).left();
        row();
        add(energy).width(150).height(24).padLeft(-30f).padTop(-5f).left();
    }

}
