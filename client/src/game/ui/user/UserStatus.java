package game.ui.user;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import game.AOGame;

public class UserStatus extends Table {

    private final static Bar hp = new Bar(Bar.Kind.HP);
    private final static Bar mana = new Bar(Bar.Kind.MANA);
    private final static Bar energy = new Bar(Bar.Kind.ENERGY);


    public UserStatus() {
        add(hp).width(getBarWidth()).height(getBarHeight()).padLeft(-getLeftPad()).left();
        row();
        add(mana).width(getBarWidth()).height(getBarHeight()).padLeft(-10f).padTop(-5f).left();
        row();
        add(energy).width(getBarWidth() / 2).height(getBarHeight()).padLeft(-getLeftPad()).padTop(-5f).left();
    }

    private float getLeftPad() {
        return getBarWidth() / 10;
    }

    private float getBarHeight() {
        float ratio = Gdx.graphics.getWidth() / AOGame.ORIGINAL_WIDTH;
        return 16 * ratio;
    }

    private float getBarWidth() {
        float ratio = Gdx.graphics.getWidth() / AOGame.ORIGINAL_WIDTH;
        return 200 * ratio;
    }

}
