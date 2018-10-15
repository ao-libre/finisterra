package ar.com.tamborindeguy.client.utils;

import ar.com.tamborindeguy.client.game.AO;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import static ar.com.tamborindeguy.client.utils.Colors.GM;

public class Fonts {

    public static final BitmapFont WHITE_FONT;
    public static final BitmapFont GM_NAME_FONT;
    public static final BitmapFont NEWBIE_NAME_FONT;
    public static final BitmapFont CITIZEN_NAME_FONT;
    public static final BitmapFont CRIMINAL_NAME_FONT;
    public static final BitmapFont DIALOG_FONT;
    public static final BitmapFont WRITING_FONT;
    public static final BitmapFont COMBAT_FONT;
    public static final BitmapFont CLAN_FONT;

    public static final GlyphLayout layout = new GlyphLayout();
    public static final GlyphLayout dialogLayout = new GlyphLayout();
    private static final String COMMODORE_FONT = "Commodore Rounded v1.2.ttf";
    private static final String FIRA_FONT = "FuraMono-Bold Powerline.otf";

    static {
        WHITE_FONT = generate(Color.WHITE, 10, Color.BLACK, 0, 0, 0);
        GM_NAME_FONT = generate(Colors.GM, 10, Color.BLACK, 0, 1, -1);
        NEWBIE_NAME_FONT = generate(Colors.NEWBIE, 10, Color.BLACK, 0, 1, -1);
        CITIZEN_NAME_FONT = generate(Colors.CITIZEN, 10, Color.BLACK, 0, 1, -1);
        CRIMINAL_NAME_FONT = generate(Colors.CRIMINAL, 10, Color.BLACK, 0, 1, -1);
        CLAN_FONT = generate(Colors.GREY, 9, Color.BLACK, 0, 1, -1);
        DIALOG_FONT = generate(Color.WHITE, 10, Color.BLACK, 0, 1, -1);
        WRITING_FONT = generate(Color.BLACK, 6, Color.WHITE, 1, 0, -2);
        COMBAT_FONT = generate(Colors.COMBAT, 9, Color.BLACK, 0, 1, -1);
    }

    private static BitmapFont generate(Color color, int size, Color borderColor, int borderWidth, int shadowOffset, int spaceX) {
        return generate(color, size, borderColor, borderWidth, shadowOffset, spaceX, COMMODORE_FONT);
    }

    private static BitmapFont generate(Color color, int size, Color borderColor, int borderWidth, int shadowOffset, int spaceX, String font) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(AO.GAME_FONTS_PATH + font));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = size;
        parameter.color = color;
        parameter.borderColor = borderColor;
        parameter.borderWidth = borderWidth;
        parameter.shadowOffsetX = shadowOffset;
        parameter.shadowOffsetY = shadowOffset;
        parameter.spaceX = spaceX;
        BitmapFont generatedFont = generator.generateFont(parameter);
        generatedFont.setUseIntegerPositions(false);
        generator.dispose(); // don't forget to dispose to avoid memory leaks!
        return generatedFont;
    }
}
