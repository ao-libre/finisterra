package ar.com.tamborindeguy.client.ui.user;

import ar.com.tamborindeguy.client.handlers.AnimationHandler;
import ar.com.tamborindeguy.client.screens.GameScreen;
import ar.com.tamborindeguy.client.utils.Colors;
import ar.com.tamborindeguy.client.utils.Fonts;
import ar.com.tamborindeguy.model.textures.BundledAnimation;
import com.artemis.E;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.sun.javafx.text.GlyphLayout;
import entity.Heading;

import static ar.com.tamborindeguy.client.utils.Fonts.WHITE_FONT;
import static ar.com.tamborindeguy.client.utils.Fonts.layout;
import static com.artemis.E.E;

public class UserImage extends Image {

    private final static Texture BACKGROUND_TEXTURE = new Texture(Gdx.files.local("data/ui/images/table-background.png"));
    private static Drawable background;
    private TextureRegion head;

    public UserImage() {
        background = new TextureRegionDrawable(new TextureRegion(BACKGROUND_TEXTURE));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        drawHead(batch);
        drawName(batch);
        super.draw(batch, parentAlpha);
    }

    private void drawName(Batch batch) {
        String userName = getUserName();
        if (!"".equals(userName)) {
            BitmapFont font = WHITE_FONT;
            layout.setText(font, userName, Colors.GREY, getWidth() - 2, GlyphLayout.LAYOUT_LEFT_TO_RIGHT, false);
            font.draw(batch, layout, getX(), getY() + getHeight() - layout.height);
        }
    }

    private String getUserName() {
        int playerId = GameScreen.getPlayer();
        if (playerId != -1) {
            E player = E(playerId);
            if (player.hasName()) {
                return player.getName().text;
            }
        }
        return "";
    }

    private void drawHead(Batch batch) {
        Color color = batch.getColor();
        batch.setColor(Color.WHITE);
        background.draw(batch, getX(), getY(), getWidth(), getHeight());
        if (getHead() != null) {
            int headW = head.getRegionWidth();
            int headH = head.getRegionHeight();
            batch.draw(head, getX() + getWidth() - 2, getY() + getHeight() - 5, 0, 0, headW, headH, 3.5f, 3.5f, 180);
        }
        batch.setColor(color);
    }

    public TextureRegion getHead() {
        if (head == null) {
            int playerId = GameScreen.getPlayer();
            if (playerId != -1) {
                E player = E(playerId);
                if (player.hasHead()) {
                    BundledAnimation headAnimation = AnimationHandler.getHeadAnimation(player.getHead(), Heading.
                            HEADING_SOUTH);
                    head = headAnimation.getGraphic();
                }
            }
        }
        return head;
    }
}
