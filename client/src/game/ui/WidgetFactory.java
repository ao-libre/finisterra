package game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import component.entity.world.CombatMessage;
import game.utils.Colors;
import game.utils.Skins;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class WidgetFactory {

    private static Supplier<Skin> skin = () -> Skins.COMODORE_SKIN;

    public static Label createLabel(String text) {
        return new Label(text, skin.get());
    }

    public static Label createDescLabel(String desc) {
        return new Label(desc, skin.get(), Labels.DESC.name);
    }

    // TODO refactor, handle colors and styles in skin.
    public static Label createConsoleLabel(String message, Color color) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(skin.get().getFont(Fonts.BIG.name), color);
        return new Label(message, labelStyle);
    }

    public static Label createTitleLabel(String name) {
        return new Label(name, skin.get(), Labels.TITLE.name);
    }

    // TODO refactor, handle colors and styles in skin.
    public static Label createCombatLabel(@NotNull CombatMessage message) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(Skins.COMODORE_SKIN.getFont("flipped-with-border"), Colors.get(message));
        labelStyle.font.setUseIntegerPositions(false);
        return new Label(message.text, labelStyle);
    }

    public static Label createFlippedLabel(String text) {
        return new Label(text, skin.get(), Labels.FLIPPED.name);
    }

    public static Label createTalkLabel(String text) {
        return new Label(text, skin.get(), Labels.SPEECH_BUBBLE.name);
    }

    public static Image createImage(Texture image) {
        return new Image(image);
    }

    public static Image createSeparatorImage() {
        return new Image(skin.get().getDrawable(Images.SEPARATOR.name));
    }

    public static Button createButton() {
        return new Button(skin.get());
    }

    public static TextButton createTextButton(String text) {
        return new TextButton(text, skin.get());
    }

    public static ImageButton createImageButton() {
        return new ImageButton(skin.get());
    }

    public static ImageButton createImageInventoryButton() {
        return new ImageButton(skin.get(), ImageButtons.INVENTORY.name);
    }

    public static ImageButton createImageSpellsButton() {
        return new ImageButton(skin.get(), ImageButtons.SPELLS.name);
    }

    public static ImageButton createImageStaffButton() {
        return new ImageButton(Skins.COMODORE_SKIN, ImageButtons.STAFF.name);
    }

    // TODO create bow image button
    public static ImageButton createImageButton(ImageButton.ImageButtonStyle style) {
        return new ImageButton(style);
    }

    public static CheckBox createCheckBox(String text) {
        return new CheckBox(text, skin.get());
    }

    public static ButtonGroup createButtonGroup() {
        return new ButtonGroup();
    }

    public static TextField createTextField(String text) {
        return new TextField(text, skin.get());
    }

    public static TextArea createTextArea(String text) {
        return new TextArea(text, skin.get());
    }

    public static List createList() {
        return new List(skin.get());
    }

    public static SelectBox createSelectBox() {
        return new SelectBox(skin.get());
    }

    public static ProgressBar createProgressBar() {
        return new ProgressBar(0, 0, 1, false, skin.get());
    }

    // TODO create loading progessbar
    public static ProgressBar createLoadingProgressBar() {
        return new ProgressBar(1, 100, 1, false, skin.get());
    }

    public static Slider createSlider() {
        return new Slider(0, 0, 1, false, skin.get());
    }

    public static Window createWindow() {
        return new Window("", skin.get());
    }

    public static Window createInventoryWindow() {
        return new Window("", skin.get(), Windows.INVENTORY.name);
    }

    public static Dialog createDialog(String title) {
        return new Dialog(title, skin.get());
    }

    enum Fonts {
        BIG("big");

        private String name;

        Fonts(String name) {
            this.name = name;
        }
    }

    enum Labels {
        DESC("desc-no-background"),
        TITLE("title-no-background"),
        FLIPPED("flipped"),
        SPEECH_BUBBLE("speech-bubble");

        private String name;

        Labels(String name) {
            this.name = name;
        }
    }

    enum Images {
        SEPARATOR("separator");

        private String name;

        Images(String name) {
            this.name = name;
        }
    }

    enum ImageButtons {
        INVENTORY("inventory"),
        SPELLS("spells"),
        STAFF("staff"),
        BOW("bow");

        private String name;

        ImageButtons(String name) {
            this.name = name;
        }
    }

    enum ProgressBars {
        LOADING("loading"),
        HP("hp"),
        MANA("mana");

        private String name;

        ProgressBars(String name) {

            this.name = name;
        }
    }

    enum Windows {
        INVENTORY("inventory"),
        SPELLS("spells");

        private String name;

        Windows(String name) {
            this.name = name;
        }
    }
}
