package test;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import game.handlers.DefaultAOAssetManager;
import game.ui.WidgetFactory;
import game.utils.Resources;
import game.utils.Skins;

/**
 * Aplicación liviana para hacer pruebas de interfaz gráfica
 */
public class UITest extends ApplicationAdapter {

    DefaultAOAssetManager assetManager;
    boolean loaded;
    Stage stage;
    Table mainTable;

    @Override
    public void create() {
        assetManager = DefaultAOAssetManager.getInstance();
        stage = new Stage();
    }

    public void createUI() {
        // Inicializamos el stage y la tabla principal
        mainTable = new Table();
        mainTable.setFillParent(true);
        // @todo Cargar esta textura por Asset Manager
        Drawable background = new TextureRegionDrawable(new Texture(Gdx.files.internal(Resources.GAME_IMAGES_PATH + "background.jpg")));
        mainTable.setBackground(background);
        stage.addActor(mainTable);

        Gdx.input.setInputProcessor(stage);

        // Tabla de login
        Window loginWindow = WidgetFactory.createWindow();
        Label emailLabel = WidgetFactory.createLabel("Email: ");
        TextField emailField = WidgetFactory.createTextField("mail@example.com");
        Label passwordLabel = WidgetFactory.createLabel("Password");
        TextField passwordField = WidgetFactory.createTextField("");
        passwordField.setPasswordCharacter('*');
        passwordField.setPasswordMode(true);
        CheckBox rememberMe = WidgetFactory.createCheckBox("Remember me");
        CheckBox seePassword = WidgetFactory.createCheckBox("See Password");

        TextButton loginButton = WidgetFactory.createTextButton("Login");
        TextButton newAccountButton = WidgetFactory.createTextButton("New account");

        loginWindow.getColor().a = 0.8f;
        loginWindow.add(emailLabel).padRight(5);
        loginWindow.add(emailField).width(250).row();
        loginWindow.add(passwordLabel).padTop(5).padRight(5);
        loginWindow.add(passwordField).padTop(5).width(250).row();
        loginWindow.add(rememberMe).padTop(20);
        loginWindow.add(loginButton).padTop(20).row();
        loginWindow.add(seePassword).padLeft(-10).padTop(30);
        loginWindow.add(newAccountButton).padTop(30).row();

        // Botones para desactivar el sonido y la musica
        CheckBox disableMusic = WidgetFactory.createCheckBox("Deshabilitar música");
        CheckBox disableSound = WidgetFactory.createCheckBox("Deshabilitar sonido");

        // Agrega la imagen del logo
        mainTable.add(
                // @todo Cargar esta textura por Asset Manager
                WidgetFactory.createImage(new Texture(Gdx.files.local("data/ui/images/logo-big.png")))
        ).pad(20).center().row();

        // Tabla botones
        Window buttonsTable = WidgetFactory.createWindow();
        buttonsTable.setMovable(false);
        // buttonsTable.background(Skins.COMODORE_SKIN.getDrawable("menu-frame"));
        buttonsTable.getTitleLabel().setColor(Color.GOLD);
        buttonsTable.getTitleLabel().setAlignment(2);
        buttonsTable.setHeight(100);
        buttonsTable.add(disableMusic).width(500).pad(10);
        buttonsTable.add(disableSound).width(400).pad(10);

        // Tabla para loguin y servers
        Table login_server = new Table();
        login_server.add(loginWindow).width(500).height(300).padLeft(10).padRight(10).padTop(10);

        // Tabla principal
        mainTable.add(login_server).row();
        mainTable.add(buttonsTable).height(100).width(920).pad(20);
        stage.setKeyboardFocus(emailField);
    }

    @Override
    public void render() {
        if (assetManager.update() && !loaded) {
            createUI();
            loaded = true;
        }
        if (loaded) {
            stage.act(Gdx.graphics.getDeltaTime());
            stage.draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    @Override
    public void dispose() {
        if (assetManager != null) assetManager.dispose();
        if (stage != null) stage.dispose();
    }
}
