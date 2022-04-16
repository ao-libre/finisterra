package game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import component.entity.world.CombatMessage;
import game.ClientConfiguration;
import game.utils.Colors;
import game.utils.Skins;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class WidgetFactory {

    private static Supplier<Skin> skin = () -> Skins.CURRENT.get();

    public static Label createLabel(String text) {
        return new Label(text, skin.get());
    }

    public static Label createSpellLabel(String text) {
        return new Label(text, skin.get(), Labels.SPELLS.name);
    }

    public static Label createUserLabel(String text) {
        return new Label(text, skin.get(), Labels.USER.name);
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
        Label.LabelStyle labelStyle = new Label.LabelStyle(Skins.CURRENT.get().getFont(Fonts.IN_GAME.name), Colors.get(message));
        labelStyle.font.setUseIntegerPositions(false);
        return new Label(message.text, labelStyle);
    }

    public static Label createBarLabel(String text) {
        return new Label(text, skin.get(), Labels.BAR.name);
    }

    public static Label createStatLabel(String text) {
        return new Label(text, skin.get(), Labels.STAT.name);
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

    public static Image createBarSeparatorImage() {
        return new Image(skin.get().getDrawable(Images.BAR_SEPARATOR.name));
    }

    public static Image createBarOverlayImage() {
        return new Image(skin.get().getDrawable(Images.BAR_OVERLAY.name));
    }

    public static Image createLineImage() {
        return new Image(skin.get().getDrawable(Images.LINE.name));
    }

    public static Button createButton() {
        return new Button(skin.get());
    }

    public static TextButton createTextButton(String text) {
        return new TextButton(text, skin.get());
    }

    public static TextButton createMagicTextButton(String text) {
        return new TextButton(text, skin.get(), TextButtons.MAGIC.name);
    }

    public static ImageButton createImageButton(ImageButtons button) {
        return new ImageButton(skin.get(), button.name);
    }

    public static ImageTextButton createImageInventoryExpandButton() {
        return new ImageTextButton("", Skins.CURRENT.get(), ImageButtons.INVENTORY.name);
    }

    public static TextButton createImageInventoryButton() {
        return new TextButton("Inventario", skin.get(), Windows.MAIN.name);
    }

    public static TextButton createImageSpellsButton() {
        return new TextButton("Hechizos", skin.get(), Windows.MAIN.name);
    }



    public static ImageButton createImageButton(ImageButton.ImageButtonStyle style) {
        return new ImageButton(style);
    }

    public static CheckBox createCheckBox(String text) {
        return new CheckBox(text, skin.get());
    }

    public static <T extends Button> ButtonGroup<T> createButtonGroup() {
        return new ButtonGroup<>();
    }

    public static TextField createTextField(String text) {
        return new TextField(text, skin.get());
    }

    public static TextArea createTextArea(String text) {
        return new TextArea(text, skin.get());
    }

    public static <T> List<T> createList() {
        return new List<>(skin.get());
    }

    public static <T> SelectBox<T> createSelectBox() {
        return new SelectBox<>(skin.get());
    }
    public static ProgressBar createProgressBar(ProgressBars bar) {
        return new ProgressBar(0, 0, 1, false, skin.get(), bar.name);
    }

    // TODO create loading progessbar

    public static ProgressBar createLoadingProgressBar() {
        return new ProgressBar(1, 100, 1, false, skin.get(), ProgressBars.LOADING.name);
    }

    public static Slider createSlider() {
        return new Slider(0, 0, 1, false, skin.get());
    }

    public static Window createWindow() {
        Window window = new Window("", skin.get());
        window.setMovable(false);
        return window;
    }

    /*
    * actor = lo que necesita que tenga las barras de desplazamiento
    * horizontalScroll = activa o desactiva el desplazamiento horizontal
    * vertivalScroll = activa o desactiva el desplazamiento vertical
    * fade = oculta las barras de desplazamiento si no se esta desplazando
    * flickScroll = permite el arrastrado con el mouse
    * */
    public static ScrollPane createScrollPane(Actor actor,boolean horizontalScroll, boolean verticalScroll, boolean fade, boolean flickScroll){
        ScrollPane scrollPane= new ScrollPane(actor);
        scrollPane.getStyle().vScrollKnob = skin.get().getDrawable( "Slider_Horizontal_Handle" );
        scrollPane.getStyle().hScrollKnob = skin.get().getDrawable( "Slider_Horizontal_Handle" );
        scrollPane.setScrollBarPositions(horizontalScroll, verticalScroll);
        scrollPane.setScrollbarsOnTop(true);
        scrollPane.setScrollbarsVisible(true);
        scrollPane.setFadeScrollBars(fade);
        scrollPane.setFlickScroll(flickScroll);
        scrollPane.setScrollingDisabled( !horizontalScroll, !verticalScroll );
        return scrollPane;
    }


    public static Table createInventoryWindow() {
        return new Table();
    }

    public static Dialog createDialog(String title) {
        return new Dialog(title, skin.get());
    }

    public static Drawable createDrawable(String drawable) {
        return skin.get().getDrawable(drawable);
    }

    public static TextureRegion createRegionTexture(String text) {
        return skin.get().getRegion(text);
    }

    public static Table createMainWindow() {
        Table table = new Table(skin.get());
        table.setBackground(createDrawable("main-window"));
        return table;
    }

    public static Table createMainTable() {
        Table table = new Table(skin.get());
        table.setBackground(createDrawable("main-background"));
        return table;
    }

    public enum Drawables {
        BAR_FILL("fill"),
        BAR_FRAME("empty"),
        CIRCLE_GLOW("UnitFrame_Main_Avatar_Overlay"),
        SLOT("button-background"),
        INVENTORY_SLOT("slot"),
        INVENTORY_SLOT_SELECTION("slot_selected"),
        INVENTORY_SLOT_OVERLAY("slot_overlay"),
        LINE("line"),
        USER_FRAME("user-stats-frame");

        public final String name;

        Drawables(String name) {
            this.name = name;
        }
    }

    enum Fonts {
        BIG("big"),
        IN_GAME("flipped");

        private final String name;

        Fonts(String name) {
            this.name = name;
        }
    }

    enum TextButtons {
        MAGIC("magic");
        private final String name;

        TextButtons(String name) {
            this.name = name;
        }
    }

    enum Labels {
        DESC("desc-no-background"),
        TITLE("title-no-background"),
        USER("user"),
        FLIPPED("flipped"),
        BAR("bar"),
        SPELLS("spells"),
        SPEECH_BUBBLE("speech-bubble"),
        STAT("ui-stat");

        private final String name;

        Labels(String name) {
            this.name = name;
        }
    }

    enum Images {
        LINE("line"),
        BAR_SEPARATOR("bar-separator"),
        BAR_OVERLAY("bar-overlay"),
        SEPARATOR("separator");

        private final String name;

        Images(String name) {
            this.name = name;
        }
    }

    public enum ImageButtons {
        BIG_DISC("big-disc"),
        ITEM_CONTAINER("inventory"),
        INVENTORY("inventory"),
        SPELLS("spells"),
        BACK("back"),
        CLOSE("close"),
        DELETE("delete"),
        SUBMIT("submit"),
        ARROW_UP("arrow-up"),
        ARROW_DOWN("arrow-down"),
        UI_HP("ui-hp"),
        UI_MANA("ui-mana"),
        UI_ENERGY("ui-energy"),
        UI_EXP("ui-exp"),
        UI_ARMOR("ui-armor"),
        UI_HELMET("ui-helmet"),
        UI_SHIELD("ui-shield"),
        UI_WEAPON("ui-weapon");

        public final String name;

        ImageButtons(String name) {
            this.name = name;
        }
    }

    public enum ProgressBars {
        LOADING("loading"),
        HP("hp"),
        MANA("mana"),
        INGAME_HP("ingame-hp"),
        INGAME_MANA("ingame-mana"),
        UI_HP("ui-hp"),
        UI_MANA("ui-mana"),
        UI_ENERGY("ui-energy"),
        UI_EXP("ui-exp");

        private final String name;

        ProgressBars(String name) {

            this.name = name;
        }
    }

    enum Windows {
        MAIN("main"),
        INVENTORY("inventory"),
        SPELLS("spells");

        private final String name;

        Windows(String name) {
            this.name = name;
        }
    }
}
