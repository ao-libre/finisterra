package game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Timer;
import game.AOGame;
import game.ClientConfiguration;
import game.handlers.AOAssetManager;
import game.handlers.MusicHandler;
import game.network.ClientResponseProcessor;
import game.network.GameNotificationProcessor;
import game.systems.network.ClientSystem;
import net.mostlyoriginal.api.network.marshal.common.MarshalState;
import shared.network.account.AccountLoginRequest;
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
            this.canConnect = false;
            connectThenLogin();
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
        ClientConfiguration config = ClientConfiguration.loadConfig(CLIENT_CONFIG); //@todo esto es un hotfix, el config tendría que cargarse en otro lado

        /* Tabla de login */
        Window loginWindow = new Window("", getSkin()); //@todo window es una ventana arrastrable
        Label usernameLabel = new Label("Username:", getSkin());
        this.username = new TextField("", getSkin());
        Label passwordLabel = new Label("Password:", getSkin());
        this.password = new TextField("", getSkin());
        this.password.setPasswordCharacter('*');
        this.password.setPasswordMode(true);
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

        TextButton newAccountButton = new TextButton("New account", getSkin());
        newAccountButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (((TextButton)actor).isPressed()) {
                    AOGame game = (AOGame) Gdx.app.getApplicationListener();
                    game.toSignUp(clientSystem);
                }
            }
        });

        loginWindow.getColor().a = 0.8f;
        loginWindow.add(usernameLabel).padRight(5);
        loginWindow.add(this.username).width(250).row();
        loginWindow.add(passwordLabel).padTop(5).padRight(5);
        loginWindow.add(this.password).padTop(5).width(250).row();
        loginWindow.add(this.rememberMe).padTop(20);
        loginWindow.add(loginButton).padTop(20).row();
        loginWindow.add();
        loginWindow.add(newAccountButton).padTop(30).row();

        /* Tabla de servidores */
        Table connectionTable = new Table((getSkin()));
        this.serverList = new List<>(getSkin());
        serverList.setItems(config.getNetwork().getServers());
        connectionTable.add(serverList).width(400).height(300); //@todo Nota: setear el size acá es redundante, pero si no se hace no se ve bien la lista. Ver (*) más abajo.

        /* Tabla principal */
        getMainTable().add(loginWindow).width(500).height(300).pad(10);
        getMainTable().add(connectionTable).width(400).height(300).pad(10); //(*) Seteando acá el size, recursivamente tendría que resizear list.
        getStage().setKeyboardFocus(this.username);
    }

    private void connectThenLogin() {

        if (this.canConnect) {

            String user = this.username.getText();
            String password = this.password.getText();

            ClientConfiguration.Network.Server server = serverList.getSelected();
            if (server == null) return;
            String ip = server.getHostname();
            int port = server.getPort();

            if (clientSystem.getState() != MarshalState.STARTING && clientSystem.getState() != MarshalState.STOPPING) {

                if (clientSystem.getState() != MarshalState.STOPPED) {
                    clientSystem.stop();
                }

                // Si no estamos tratando de conectarnos al servidor, intentamos conectarnos.
                if (clientSystem.getState() == MarshalState.STOPPED) {

                    // Seteamos la info. del servidor al que nos vamos a conectar.
                    clientSystem.getKryonetClient().setHost(ip);
                    clientSystem.getKryonetClient().setPort(port);

                    // Inicializamos la conexion.
                    clientSystem.start();

                    // Si pudimos conectarnos, mandamos la peticion para loguearnos a la cuenta.
                    if (clientSystem.getState() == MarshalState.STARTED) {

                        // Enviamos la peticion de inicio de sesion.
                        clientSystem.getKryonetClient().sendToAll(new AccountLoginRequest(user, password));

                    } else if (clientSystem.getState() == MarshalState.FAILED_TO_START) {
                        this.canConnect = true;

                        AOAssetManager assetManager = AOGame.getGlobalAssetManager();

                        // Mostramos un mensaje de error.
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
