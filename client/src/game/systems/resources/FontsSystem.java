package game.systems.resources;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import game.handlers.DefaultAOAssetManager;
import net.mostlyoriginal.api.system.core.PassiveSystem;

@Wire
public class FontsSystem extends PassiveSystem {
    @Wire
    private DefaultAOAssetManager assetManager;

    @Override
    protected void initialize() {
        super.initialize();
        initFonts();
    }

    private void initFonts() {
        //        WHITE_FONT = gui(Color.WHITE, 5, Color.BLACK, 0, 0, 0, COMMODORE_FONT);
//        WHITE_FONT_WITH_BORDER = gui(Color.WHITE, 6, Color.BLACK, 1, 1, 0, COMMODORE_FONT);
//        CONSOLE_FONT = gui(Color.WHITE, 8, Color.BLACK, 0, 0, 0, COMMODORE_FONT);
//        GM_NAME_FONT = generate(Colors.GM, 13, Color.BLACK, 0, 1, -1, COMMODORE_FONT);
//        NEWBIE_NAME_FONT = generate(Colors.NEWBIE, 13, Color.BLACK, 0, 1, -1, COMMODORE_FONT);
//        CITIZEN_NAME_FONT = generate(Colors.CITIZEN, 13, Color.BLACK, 0, 1, -1, COMMODORE_FONT);
//        CRIMINAL_NAME_FONT = generate(Colors.CRIMINAL, 13, Color.BLACK, 0, 1, -1, COMMODORE_FONT);
//        CLAN_FONT = generate(Colors.GREY, 11, Color.BLACK, 0, 1, -1, COMMODORE_FONT);
//        DIALOG_FONT = generate(Color.WHITE, 11, Color.BLACK, 0, 1, -1, COMMODORE_FONT);
//        MAGIC_FONT = generate(Colors.MANA, 11, Color.BLACK, 0, 1, -1, COMMODORE_FONT);
//        ENERGY_FONT = generate(Colors.YELLOW, 11, Color.BLACK, 0, 1, -1, COMMODORE_FONT);
//        WRITING_FONT = generate(Color.BLACK, 6, Color.WHITE, 1, 0, -2, COMMODORE_FONT);
//        COMBAT_FONT = generate(Colors.COMBAT, 11, Color.BLACK, 0, 1, -1, COMMODORE_FONT);
//        STAB_FONT = generate(Color.WHITE, 13, Color.BLACK, 0, 1, -1, COMMODORE_FONT);
//        MAGIC_COMBAT_FONT = generate(Colors.MANA, 11, Color.BLACK, 0, 1, -1, COMMODORE_FONT);
    }

    private Skin getSkin() {
        return assetManager.getSkin();
    }

    public BitmapFont getShadowedFont() {
        return getSkin().getFont("big-shadow");
    }

    public BitmapFont getNormalFont() {
        return getSkin().getFont("small");
    }

    public BitmapFont getFlippedFont() {
        return getSkin().getFont("flipped");
    }
}
