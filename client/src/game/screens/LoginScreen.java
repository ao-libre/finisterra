package game.screens;

import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import game.ClientConfiguration;
import game.ClientConfiguration.Network.DefaultServer;
import game.handlers.MusicHandler;
import game.systems.network.ClientSystem;
import net.mostlyoriginal.api.network.marshal.common.MarshalState;
import shared.interfaces.Hero;
import shared.network.lobby.JoinLobbyRequest;

public class LoginScreen extends AbstractScreen {

    private ClientSystem clientSystem;
    private World world;
    private ClientConfiguration config = ClientConfiguration.loadConfig("");


    public LoginScreen() {
        super();
        init();
    }

    private void init() {
        WorldConfigurationBuilder builder = new WorldConfigurationBuilder();
        DefaultServer defaultServer = config.getNetwork().getDefaultServer();
        clientSystem = new ClientSystem(defaultServer.getHostname(), defaultServer.getPort());
        world = new World(builder.with(clientSystem).build());
        MusicHandler.playMusic(101);
    }

    @Override
    void createContent() {
        Label userLabel = new Label("User", getSkin());
        TextField username = new TextField("", getSkin());
        username.setMessageText("username");

        Label heroLabel = new Label("Hero", getSkin());
        SelectBox<Hero> heroSelect = new SelectBox<>(getSkin());
        final Array<Hero> heroes = new Array<>();
        Hero.getHeroes().forEach(hero -> heroes.add(hero));
        heroSelect.setItems(heroes);

        Table connectionTable = new Table((getSkin()));

        Label ipLabel = new Label("IP: ", getSkin());
        DefaultServer defaultServer = config.getNetwork().getDefaultServer();
        TextField ipText = new TextField(defaultServer.getHostname(), getSkin());

        Label portLabel = new Label("Port: ", getSkin());
        TextField portText = new TextField("" + defaultServer.getPort(), getSkin());
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

        getMainTable().add(userLabel);
        getMainTable().row();
        getMainTable().add(username).width(200);
        getMainTable().row();
        getMainTable().add(heroLabel).padTop(20);
        getMainTable().row();
        getMainTable().add(heroSelect).width(200);
        getMainTable().row();
        getMainTable().add(loginButton).padTop(20).expandX().row();

        connectionTable.add(ipLabel);
        connectionTable.add(ipText).width(500);
        connectionTable.add(portLabel);
        connectionTable.add(portText);
        connectionTable.setPosition(420, 30); //Hardcoded

        getMainTable().add(connectionTable);
        getStage().setKeyboardFocus(username);
    }

    @Override
    public void render(float delta) {
        world.process();
        super.render(delta);
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
