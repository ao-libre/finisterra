package game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import static game.systems.render.world.RenderingSystem.SCALE;

public class Fonts {

    public static final BitmapFont WHITE_FONT;
    public static final BitmapFont WHITE_FONT_WITH_BORDER;
    public static final BitmapFont CONSOLE_FONT;
    public static final BitmapFont GM_NAME_FONT;
    public static final BitmapFont NEWBIE_NAME_FONT;
    public static final BitmapFont CITIZEN_NAME_FONT;
    public static final BitmapFont CRIMINAL_NAME_FONT;
    public static final BitmapFont DIALOG_FONT;
    public static final BitmapFont MAGIC_FONT;
    public static final BitmapFont ENERGY_FONT;
    public static final BitmapFont WRITING_FONT;
    public static final BitmapFont STAB_FONT;
    public static final BitmapFont COMBAT_FONT;
    public static final BitmapFont MAGIC_COMBAT_FONT;
    public static final BitmapFont CLAN_FONT;

    public static final GlyphLayout layout = new GlyphLayout();
    public static final GlyphLayout dialogLayout = new GlyphLayout();
    private static final String COMMODORE_FONT = "Commodore Rounded v1.2.ttf";
    private static final String FIRA_FONT = "FuraMono-Bold Powerline.otf";

    static {
        WHITE_FONT = gui(Color.WHITE, 5, Color.BLACK, 0, 0, 0, COMMODORE_FONT);
        WHITE_FONT_WITH_BORDER = gui(Color.WHITE, 6, Color.BLACK, 0, 1, 0, COMMODORE_FONT);
        CONSOLE_FONT = gui(Color.WHITE, 8, Color.BLACK, 0, 0, 0, COMMODORE_FONT);
        GM_NAME_FONT = generate(Colors.GM, 9, Color.BLACK, 0, 1, -1, COMMODORE_FONT);
        NEWBIE_NAME_FONT = generate(Colors.NEWBIE, 9, Color.BLACK, 0, 1, -1, COMMODORE_FONT);
        CITIZEN_NAME_FONT = generate(Colors.CITIZEN, 9, Color.BLACK, 0, 1, -1, COMMODORE_FONT);
        CRIMINAL_NAME_FONT = generate(Colors.CRIMINAL, 9, Color.BLACK, 0, 1, -1, COMMODORE_FONT);
        CLAN_FONT = generate(Colors.GREY, 9, Color.BLACK, 0, 1, -1, COMMODORE_FONT);
        DIALOG_FONT = generate(Color.WHITE, 9, Color.BLACK, 0, 1, -1, COMMODORE_FONT);
        MAGIC_FONT = generate(Colors.MANA, 9, Color.BLACK, 0, 1, -1, COMMODORE_FONT);
        ENERGY_FONT = generate(Colors.YELLOW, 9, Color.BLACK, 0, 1, -1, COMMODORE_FONT);
        WRITING_FONT = generate(Color.BLACK, 6, Color.WHITE, 1, 0, -2, COMMODORE_FONT);
        COMBAT_FONT = generate(Colors.COMBAT, 10, Color.BLACK, 0, 1, -1, COMMODORE_FONT);
        STAB_FONT = generate(Color.WHITE, 11, Color.BLACK, 0, 1, -1, COMMODORE_FONT);
        MAGIC_COMBAT_FONT = generate(Colors.MANA, 10, Color.BLACK, 0, 1, -1, COMMODORE_FONT);
    }

    private static BitmapFont generate(Color color, int size, Color borderColor, int borderWidth, int shadowOffset, int spaceX, String commodoreFont) {
        return generate(color, size, borderColor, borderWidth, shadowOffset, spaceX, true, COMMODORE_FONT);
    }

    private static BitmapFont gui(Color color, int size, Color borderColor, int borderWidth, int shadowOffset, int spaceX, String commodoreFont) {
        return generate(color, size, borderColor, borderWidth, shadowOffset, spaceX, false, COMMODORE_FONT);
    }

    private static BitmapFont generate(Color color, int size, Color borderColor, int borderWidth, int shadowOffset, int spaceX, boolean flip, String font) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(Resources.GAME_FONTS_PATH + font));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.magFilter = Texture.TextureFilter.Linear;
        parameter.minFilter = Texture.TextureFilter.Linear;
        parameter.size = (int) (size * SCALE);
        parameter.color = color;
        parameter.borderColor = borderColor;
        parameter.borderWidth = borderWidth;
        parameter.shadowOffsetX = shadowOffset;
        parameter.shadowOffsetY = shadowOffset;
        parameter.spaceX = spaceX;
        parameter.flip = flip;
        BitmapFont generatedFont = generator.generateFont(parameter);
        generatedFont.setUseIntegerPositions(false);
        generator.dispose(); // don't forget to dispose to avoid memory leaks!
        return generatedFont;
    }
}
