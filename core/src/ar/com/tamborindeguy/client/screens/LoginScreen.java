package ar.com.tamborindeguy.client.screens;

import ar.com.tamborindeguy.client.game.AO;
import ar.com.tamborindeguy.client.handlers.AnimationsHandler;
import ar.com.tamborindeguy.client.handlers.DescriptorsHandler;
import ar.com.tamborindeguy.client.handlers.MapHandler;
import ar.com.tamborindeguy.client.handlers.ParticlesHandler;
import ar.com.tamborindeguy.client.utils.Skins;
import ar.com.tamborindeguy.network.login.LoginRequest;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class LoginScreen extends ScreenAdapter {

    private Stage stage;
    private AO game;
    private TextButton loginButton;

    public LoginScreen(AO game) {
        this.game = game;
        stage = new Stage();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        createUI();
        // Load resources
        Gdx.app.log("Loading", "Loading descriptors...");
        DescriptorsHandler.load();
        Gdx.app.log("Loading", "Loading animations...");
        AnimationsHandler.load();
        Gdx.app.log("Loading", "Loading maps...");
        MapHandler.load();
        Gdx.app.log("Loading", "Loading particles...");
        ParticlesHandler.load();
        Gdx.app.log("Loading", "Finish loading");
        loginButton.setDisabled(false);
    }

    private void createUI() {
        Table login = new Table(Skins.COMODORE_SKIN);

        Label userLabel = new Label("User", Skins.COMODORE_SKIN);
        TextField username = new TextField("guidota", Skins.COMODORE_SKIN);

        Label passLabel = new Label("Password", Skins.COMODORE_SKIN);
        TextField password = new TextField("", Skins.COMODORE_SKIN);
        password.setPasswordMode(true);
        loginButton = new TextButton("Connect", Skins.COMODORE_SKIN);
        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String user = username.getText();
                String pass = password.getText();
                // request login
                game.getClient().sendToAll(new LoginRequest(user, pass));
                game.gameScene();
            };
        });
        loginButton.setDisabled(true);

        login.setFillParent(true);

        login.add(userLabel);
        login.row();
        login.add(username).width(200);
        login.row();
        login.add(passLabel);
        login.row();
        login.add(password).width(200);
        login.row();
        login.add(loginButton).expandX();

        stage.addActor(login);
        stage.setKeyboardFocus(username);
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }
}
