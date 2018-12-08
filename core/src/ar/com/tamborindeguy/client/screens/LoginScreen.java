package ar.com.tamborindeguy.client.screens;

import ar.com.tamborindeguy.client.game.AOGame;
import ar.com.tamborindeguy.client.network.KryonetClientMarshalStrategy;
import ar.com.tamborindeguy.client.systems.network.ClientSystem;
import ar.com.tamborindeguy.client.utils.Skins;
import ar.com.tamborindeguy.interfaces.Hero;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import net.mostlyoriginal.api.network.marshal.common.MarshalState;
import net.mostlyoriginal.api.network.marshal.common.MarshalStrategy;

public class LoginScreen extends ScreenAdapter {

    private Stage stage;
    private TextButton loginButton;

    public LoginScreen() {
        stage = new Stage();
        createUI();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    private void createUI() {
        Table login = new Table(Skins.COMODORE_SKIN);

        Label userLabel = new Label("User", Skins.COMODORE_SKIN);
        TextField username = new TextField("guidota", Skins.COMODORE_SKIN);

        Label heroLabel = new Label("Hero", Skins.COMODORE_SKIN);
        SelectBox<Hero> heroSelect = new SelectBox<Hero>(Skins.COMODORE_SKIN);
        heroSelect.setItems(Hero.WARRIOR, Hero.MAGICIAN, Hero.ROGUE, Hero.PALADIN, Hero.PRIEST);

        loginButton = new TextButton("Connect", Skins.COMODORE_SKIN);
        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String user = username.getText();
                int heroID = heroSelect.getSelected().ordinal();

                loginButton.setDisabled(true);
                connectThenLogin(user, heroID);
                loginButton.setDisabled(false);
            }

        });

        login.setFillParent(true);

        login.add(userLabel);
        login.row();
        login.add(username).width(200);
        login.row();
        login.add(heroLabel).padTop(20);
        login.row();
        login.add(heroSelect).width(200);
        login.row();
        login.add(loginButton).padTop(20).expandX();

        stage.addActor(login);
        stage.setKeyboardFocus(username);
    }

    private void connectThenLogin(String user, int classId) {
        // establish connection
        AOGame game = (AOGame) Gdx.app.getApplicationListener();
        ClientSystem clientSystem = game.getClientSystem();
        if(clientSystem.getState() != MarshalState.STARTING && clientSystem.getState() != MarshalState.STOPPING) {
            if(clientSystem.getState() != MarshalState.STOPPED)
                clientSystem.stop();
            if(clientSystem.getState() == MarshalState.STOPPED) {
                clientSystem.start();
                if(clientSystem.getState() == MarshalState.STARTED) {
                    clientSystem.login(user, classId);
                }
                else if(clientSystem.getState() == MarshalState.FAILED_TO_START) {
                    Dialog dialog = new Dialog("Network error", Skins.COMODORE_SKIN);
                    dialog.text("Failed to connect! :(");
                    dialog.button("OK");
                    dialog.show(stage);
                }
            }
        }
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }
}
