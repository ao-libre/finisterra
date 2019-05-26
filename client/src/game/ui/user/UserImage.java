package game.ui.user;

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
import com.sun.javafx.text.GlyphLayout;
import entity.character.states.Heading;
import game.handlers.AnimationHandler;
import game.screens.GameScreen;
import game.utils.Colors;
import game.utils.Fonts;
import model.textures.BundledAnimation;

import static com.artemis.E.E;

public class UserImage extends Image {

    private final static Texture BACKGROUND_TEXTURE = new Texture(Gdx.files.local("data/ui/images/table-background.png"));
    private static Drawable background;
    private TextureRegion head;

    public UserImage() {
        background = new TextureRegionDrawable(new TextureRegion(BACKGROUND_TEXTURE));
        setWidth(64);
        setHeight(64);
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
            BitmapFont font = Fonts.WHITE_FONT;
            Fonts.layout.setText(font, userName, Colors.GREY, getWidth() - 2, GlyphLayout.LAYOUT_LEFT_TO_RIGHT, false);
            font.draw(batch, Fonts.layout, getX(), getY() + getHeight() - Fonts.layout.height);
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
            batch.draw(head, getX() + headW, getY() - 50, headW, headH);
        }
        batch.setColor(color);
    }

    public void refresh() {
        head = null;
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
                    head = new TextureRegion(head);
                    head.flip(false, true);
                }
            }
        }
        return head;
    }
}
