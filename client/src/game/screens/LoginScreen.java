package game.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
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

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static game.utils.Resources.CLIENT_CONFIG;

public class LoginScreen extends AbstractScreen {

    private ClientSystem clientSystem;

    private TextField username;
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
        Cell<Image> imageCell = addTitle();
        Image logo = imageCell.getActor();
        imageCell.row();
        logo.addAction(
                sequence(
                        fadeOut(0),
                        moveBy(0, 20f, 0),
                        parallel(fadeIn(2),
                                moveBy(0, -20, 0.5f))));
        Window loginWindow = new Window("", getSkin(), "content");
        loginWindow.pad(20);

        Table userLogin = new Table(getSkin());
        userLogin.pad(20);
        this.username = new TextField("", getSkin(), "ui");
        username.setMessageText("User Name");

        TextButton loginButton = new TextButton("Connect", getSkin(), "ui");
        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                connectThenLogin();
            }
        });

        userLogin.add(new Label("User", getSkin(), "ui")).row();
        userLogin.add(username).growX().row();
        userLogin.add(loginButton).growX();

        Table connectionTable = new Table((getSkin()));
        connectionTable.pad(20);
        connectionTable.add(new Label("Servers", getSkin(), "ui")).row();
        this.serverList = new List<>(getSkin(), "ui");
        serverList.setItems(config.getNetwork().getServers());
        connectionTable.add(new ScrollPane(serverList, getSkin(), "ui")).growX();

        loginWindow.add(userLogin).growX().row();
        loginWindow.add(connectionTable).padTop(40).growX();
        userLogin.addAction(sequence(fadeOut(0), fadeIn(2f)));
        connectionTable.addAction(sequence(fadeOut(0), fadeIn(2f)));
        getMainTable().add(loginWindow).prefHeight(350);
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

    public void resetConnect() {
        Dialog dialog = new Dialog("Network error", getSkin(), "ui");
        dialog.text("Wrong client-server communication");
        dialog.button("OK");
        dialog.show(getStage());
        this.canConnect = true;
    }
}
