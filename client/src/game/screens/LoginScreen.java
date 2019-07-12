package game.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import game.ClientConfiguration;
import game.ClientConfiguration.Network.DefaultServer;
import game.network.ClientResponseProcessor;
import game.network.GameNotificationProcessor;
import game.systems.network.ClientSystem;
import net.mostlyoriginal.api.network.marshal.common.MarshalState;
import shared.interfaces.Hero;
import shared.network.lobby.JoinLobbyRequest;

import static game.utils.Resources.CLIENT_CONFIG;

public class LoginScreen extends AbstractScreen {

    private ClientSystem clientSystem;
    private ClientConfiguration config;

    private TextField username;
    private SelectBox<Hero> heroSelect;
    private TextField ipText;
    private TextField portText;

    public LoginScreen() {
        super();
        init();
    }

    @Override
    protected void keyPressed(int keyCode) {
         if (keyCode == Input.Keys.ENTER) {
             String user = username.getText();
             Hero hero = heroSelect.getSelected();
             String ip = ipText.getText();
             int port = Integer.valueOf(portText.getText());

             connectThenLogin(ip, port, user, hero);
         }
    }

    private void init() {
        DefaultServer defaultServer = config.getNetwork().getDefaultServer();
        clientSystem = new ClientSystem(defaultServer.getHostname(), defaultServer.getPort());
        clientSystem.setNotificationProcessor(new GameNotificationProcessor());
        clientSystem.setResponseProcessor(new ClientResponseProcessor());
        // TODO MusicHandler.playMusic(101);
    }

    @Override
    void createContent() {
        config = ClientConfiguration.loadConfig(CLIENT_CONFIG); // @todo hotfix

        Window loginWindow = new Window("", getSkin());
        Label userLabel = new Label("User", getSkin());
        this.username = new TextField("", getSkin());
        username.setMessageText("User Name");

        Label heroLabel = new Label("Hero", getSkin());
        this.heroSelect = new SelectBox<>(getSkin());
        final Array<Hero> heroes = new Array<>();
        Hero.getHeroes().forEach(heroes::add);
        heroSelect.setItems(heroes);

        Table connectionTable = new Table((getSkin()));

        Label ipLabel = new Label("IP: ", getSkin());
        DefaultServer defaultServer = config.getNetwork().getDefaultServer();
        this.ipText = new TextField(defaultServer.getHostname(), getSkin());

        Label portLabel = new Label("Port: ", getSkin());
        this.portText = new TextField("" + defaultServer.getPort(), getSkin());
        portText.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());

        TextButton loginButton = new TextButton("Connect", getSkin());
        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String user = username.getText();
                Hero hero = heroSelect.getSelected();
                String ip = ipText.getText();
                int port = Integer.valueOf(portText.getText());

                loginButton.setDisabled(true);
                connectThenLogin(ip, port, user, hero);
                loginButton.setDisabled(false);
            }

        });

        loginWindow.getColor().a = 0.8f;
        loginWindow.add(userLabel);
        loginWindow.row();
        loginWindow.add(username).width(200);
        loginWindow.row();
        loginWindow.add(heroLabel).padTop(20);
        loginWindow.row();
        loginWindow.add(heroSelect).width(200);
        loginWindow.row();
        loginWindow.add(loginButton).padTop(20).expandX().row();
        getMainTable().add(loginWindow).width(400).height(300).row();

        connectionTable.add(ipLabel);
        connectionTable.add(ipText).width(500);
        connectionTable.add(portLabel);
        connectionTable.add(portText);
        connectionTable.align(Align.center);
        connectionTable.setVisible(true);
        getMainTable().add(connectionTable);
        getStage().setKeyboardFocus(username);
    }

    private void connectThenLogin(String ip, int port, String user, Hero hero) {
        if (clientSystem.getState() != MarshalState.STARTING && clientSystem.getState() != MarshalState.STOPPING) {
            if (clientSystem.getState() != MarshalState.STOPPED)
                clientSystem.stop();
            if (clientSystem.getState() == MarshalState.STOPPED) {

                clientSystem.getKryonetClient().setHost(ip);
                clientSystem.getKryonetClient().setPort(port);

                clientSystem.start();
                if (clientSystem.getState() == MarshalState.STARTED) {
                    clientSystem.getKryonetClient().sendToAll(new JoinLobbyRequest(user, hero));
                } else if (clientSystem.getState() == MarshalState.FAILED_TO_START) {
                    Dialog dialog = new Dialog("Network error", getSkin());
                    dialog.text("Failed to connect! :(");
                    dialog.button("OK");
                    dialog.show(getStage());
                }
            }
        }
    }

    public ClientSystem getClientSystem() {
        return clientSystem;
    }
}
