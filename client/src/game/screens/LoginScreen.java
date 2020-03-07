package game.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import game.AOGame;
import game.ClientConfiguration;
import game.handlers.AOAssetManager;
import game.handlers.MusicHandler;
import game.network.ClientResponseProcessor;
import game.network.GameNotificationProcessor;
import game.systems.network.ClientSystem;
import net.mostlyoriginal.api.network.marshal.common.MarshalState;
import shared.network.lobby.JoinLobbyRequest;
import shared.util.Messages;

import static game.utils.Resources.CLIENT_CONFIG;

public class LoginScreen extends AbstractScreen {

    private ClientSystem clientSystem;

    private TextField username;
    private TextField password;
    private CheckBox rememberMe;
    private List<ClientConfiguration.Network.Server> serverList;

    private boolean canConnect = true;

    public LoginScreen() {
        super();
        init();
        // utilice bgmusic  para subir gradualmente el sonido.
        bGMusic ();
    }

    void bGMusic() {
        Music firstBGMusic = MusicHandler.FIRSTBGM;
        firstBGMusic.setVolume ( 0 );
        firstBGMusic.play ( );
        firstBGMusic.setLooping ( true );
        // incrementa el sonido gradualmente hasta llegar al 34%
        float MUSIC_FADE_STEP = 0.01f;
        Timer.schedule ( new Timer.Task ( ) {
            @Override
            public void run() {
                if (firstBGMusic.getVolume ( ) < 0.34f)
                    firstBGMusic.setVolume ( firstBGMusic.getVolume ( ) + MUSIC_FADE_STEP );
                else {
                    this.cancel ( );
                }
            }
        }, 0, 0.6f );
    }

    @Override
    protected void keyPressed(int keyCode) {
        if (keyCode == Input.Keys.ENTER && this.canConnect) {
            //Connect
            connectThenLogin();

            //Prevent multiple simultaneous connections.
            this.canConnect = false;
        }
    }

    private void init() {
        clientSystem = new ClientSystem("127.0.0.1", 7666); // @todo implement empty constructor
        clientSystem.setNotificationProcessor(new GameNotificationProcessor());
        clientSystem.setResponseProcessor(new ClientResponseProcessor());

        // TODO MusicHandler.playMusic(101);
    }

    @Override
    void createContent() {
        ClientConfiguration config = ClientConfiguration.loadConfig(CLIENT_CONFIG); // @todo hotfix

        //@todo centrar la tabla de login
        //@todo cambiar window por table (window está pensado para ventanas)

        /** Tabla de login */
        Window loginWindow = new Window("", getSkin());
        Label usernameLabel = new Label("Username or email:", getSkin());
        this.username = new TextField("", getSkin());
        Label passwordLabel = new Label("Password:", getSkin());
        this.password = new TextField("", getSkin());
        this.rememberMe = new CheckBox("Remember me", getSkin());

        TextButton loginButton = new TextButton("Login", getSkin());
        loginButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (((TextButton)actor).isPressed()) {
                    connectThenLogin();
                }
            }
        });

        TextButton registerButton = new TextButton("New account", getSkin());
        loginButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (((TextButton)actor).isPressed()) {
                    //@todo ui creacion de cuenta
                }
            }
        });

        loginWindow.getColor().a = 0.8f;
        loginWindow.add(usernameLabel);
        loginWindow.add(this.username).width(250).row();
        loginWindow.add(passwordLabel).padTop(5);
        loginWindow.add(this.password).padTop(5).width(250).row();
        loginWindow.add(this.rememberMe).padTop(20);
        loginWindow.add(loginButton).padTop(20).expandX().row();
        loginWindow.add();
        loginWindow.add(registerButton).padTop(30).row();

        /** Tabla de servidores */
        Table connectionTable = new Table((getSkin()));

        this.serverList = new List<>(getSkin());
        serverList.setItems(config.getNetwork().getServers());

        connectionTable.add(serverList).width(300);
        connectionTable.align(Align.center);
        connectionTable.setVisible(true);

        /** Tabla principal */
        getMainTable().add(loginWindow).width(500).height(300);
        getMainTable().add(connectionTable).width(400).height(300);
        getStage().setKeyboardFocus(username);
    }

    private void connectThenLogin() {

        if (this.canConnect) {
            String user = username.getText();

            ClientConfiguration.Network.Server server = serverList.getSelected();
            if (server == null) return;
            String ip = server.getHostname();
            int port = server.getPort();

            if (clientSystem.getState() != MarshalState.STARTING && clientSystem.getState() != MarshalState.STOPPING) {
                if (clientSystem.getState() != MarshalState.STOPPED)
                    clientSystem.stop();
                if (clientSystem.getState() == MarshalState.STOPPED) {

                    clientSystem.getKryonetClient().setHost(ip);
                    clientSystem.getKryonetClient().setPort(port);

                    clientSystem.start();
                    if (clientSystem.getState() == MarshalState.STARTED) {
                        clientSystem.getKryonetClient().sendToAll(new JoinLobbyRequest(user));

                    } else if (clientSystem.getState() == MarshalState.FAILED_TO_START) {
                        AOAssetManager assetManager = AOGame.getGlobalAssetManager();

                        Dialog dialog = new Dialog(assetManager.getMessages(Messages.FAILED_TO_CONNECT_TITLE), getSkin());
                        dialog.text(assetManager.getMessages(Messages.FAILED_TO_CONNECT_DESCRIPTION));
                        dialog.button("OK");
                        dialog.show(getStage());
                    }
                }
            }
        }
    }

    public ClientSystem getClientSystem() {
        return clientSystem;
    }
}
