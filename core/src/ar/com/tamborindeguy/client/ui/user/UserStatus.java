package ar.com.tamborindeguy.client.ui.user;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class UserStatus extends Table {

    private final static Texture BACKGROUND_TEXTURE = new Texture(Gdx.files.local("data/ui/images/status-background.png"));
    private static Drawable background;
    private final static Bar hp = new Bar(Bar.Kind.HP);
    private final static Bar mana = new Bar(Bar.Kind.MANA);
    private final static Bar energy = new Bar(Bar.Kind.ENERGY);

    public UserStatus() {
        background = new TextureRegionDrawable(new TextureRegion(BACKGROUND_TEXTURE));
        pad(2);
        add(hp).width(Bar.TOTAL_WIDTH).height(Bar.TOTAL_HEIGHT).padLeft(2).padRight(2).expandX();
        row();
        add(mana).width(Bar.TOTAL_WIDTH).height(Bar.TOTAL_HEIGHT).padLeft(2).padRight(2).expandX();
        row();
        add(energy).width(Bar.TOTAL_WIDTH).height(Bar.TOTAL_HEIGHT).padLeft(2).padRight(2).expandX();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = batch.getColor();
        batch.setColor(Color.WHITE);
        background.draw(batch, getX(), getY(), getWidth(), getHeight());
        batch.setColor(color);
        super.draw(batch, parentAlpha);
    }
}
