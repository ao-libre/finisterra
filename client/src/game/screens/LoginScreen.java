package game.screens;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Timer;
import game.ClientConfiguration;
import game.handlers.DefaultAOAssetManager;
import game.systems.network.ClientSystem;
import game.systems.resources.MusicSystem;
import net.mostlyoriginal.api.network.marshal.common.MarshalState;
import shared.network.account.AccountLoginRequest;
import shared.util.Messages;

@Wire
public class LoginScreen extends AbstractScreen {

    @Wire
    private DefaultAOAssetManager assetManager;
    private ClientConfiguration clientConfiguration;
    private ClientSystem clientSystem;
    private ScreenManager screenManager;

    private TextField emailField;
    private TextField passwordField;
    private CheckBox rememberMe; //@todo implementar remember me
    private CheckBox seePassword;
    private TextButton loginButton;
    private List<ClientConfiguration.Network.Server> serverList;

    public LoginScreen() {
        // utilice bgmusic  para subir gradualmente el sonido.
        bGMusic(); //@todo mover esto fuera del constructor
    }

    void bGMusic() {
        Music firstBGMusic = MusicSystem.FIRSTBGM;
        firstBGMusic.setVolume(0);
        firstBGMusic.play();
        firstBGMusic.setLooping(true);
        // incrementa el sonido gradualmente hasta llegar al 34%
        float MUSIC_FADE_STEP = 0.01f;
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (firstBGMusic.getVolume() < 0.34f)
                    firstBGMusic.setVolume(firstBGMusic.getVolume() + MUSIC_FADE_STEP);
                else {
                    this.cancel();
                }
            }
        }, 0, 0.6f);
    }

    //    @Override
//    protected void keyPressed(int keyCode) {
//        if (keyCode == Input.Keys.ENTER && this.canConnect) {
//            this.canConnect = false;
//            connectThenLogin();
//        }
//    }

    @Override
    protected void createUI() {
        ClientConfiguration.Account account = clientConfiguration.getAccount();

        /* Tabla de login */
        Window loginWindow = new Window("", getSkin()); //@todo window es una ventana arrastrable
        Label emailLabel = new Label("Email:", getSkin());
        emailField = new TextField(account.getEmail(), getSkin());
        Label passwordLabel = new Label("Password:", getSkin());
        passwordField = new TextField(account.getPassword(), getSkin());
        passwordField.setPasswordCharacter('*');
        passwordField.setPasswordMode(true);
        rememberMe = new CheckBox("Remember me", getSkin());
        seePassword = new CheckBox("See Password", getSkin());
        seePassword.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (((CheckBox) actor).isPressed()) {
                    passwordField.setPasswordMode(!passwordField.isPasswordMode());
                }
            }
        });

        loginButton = new TextButton("Login", getSkin());
        loginButton.addListener(new LoginButtonListener());

        TextButton newAccountButton = new TextButton("New account", getSkin());
        newAccountButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (((TextButton) actor).isPressed()) {
                    screenManager.to(ScreenEnum.SIGN_UP);
                }
            }
        });

        loginWindow.getColor().a = 0.8f;
        loginWindow.add(emailLabel).padRight(5);
        loginWindow.add(emailField).width(250).row();
        loginWindow.add(passwordLabel).padTop(5).padRight(5);
        loginWindow.add(passwordField).padTop(5).width(250).row();
        loginWindow.add(rememberMe).padTop(20);
        loginWindow.add(loginButton).padTop(20).row();
        loginWindow.add(seePassword).padLeft(-10).padTop(30);
        loginWindow.add(newAccountButton).padTop(30).row();

        /* Tabla de servidores */
        Table connectionTable = new Table((getSkin()));
        serverList = new List<>(getSkin());
        serverList.setItems(clientConfiguration.getNetwork().getServers());
        connectionTable.add(serverList).width(400).height(300); //@todo Nota: setear el size acá es redundante, pero si no se hace no se ve bien la lista. Ver (*) más abajo.

        /* Tabla principal */
        getMainTable().add(loginWindow).width(500).height(300).pad(10);
        getMainTable().add(connectionTable).width(400).height(300).pad(10); //(*) Seteando acá el size, recursivamente tendría que resizear list.
        getStage().setKeyboardFocus(emailField);
    }

    private class LoginButtonListener extends ChangeListener {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
            if (((TextButton) actor).isPressed()) {
                //El boton fue apretado
                loginButton.setDisabled(true);
                Timer.schedule(new Timer.Task() { //@todo implementar API que tome lambdas () -> {}
                    @Override
                    public void run() {
                        loginButton.setDisabled(false);
                    }
                }, 2);

                String email = emailField.getText();
                String password = passwordField.getText();

                clientConfiguration.getAccount().setEmail(email);
                clientConfiguration.getAccount().setPassword(password);
                // clientConfiguration.save(); TODO this is breaking all

                ClientConfiguration.Network.Server server = serverList.getSelected();
                if (server == null) return;
                String ip = server.getHostname();
                int port = server.getPort();

                //@todo encapsular todo este chequeo en el cliente
                if (clientSystem.getState() != MarshalState.STARTING && clientSystem.getState() != MarshalState.STOPPING) {

                    if (clientSystem.getState() != MarshalState.STOPPED) {
                        clientSystem.stop();
                    }

                    // Si no estamos tratando de conectarnos al servidor, intentamos conectarnos.
                    if (clientSystem.getState() == MarshalState.STOPPED) {

                        // Seteamos la info. del servidor al que nos vamos a conectar.
                        clientSystem.setHost(ip, port);


                        // Inicializamos la conexion.
                        clientSystem.start();

                        // Si pudimos conectarnos, mandamos la peticion para loguearnos a la cuenta.
                        if (clientSystem.getState() == MarshalState.STARTED) {

                            // Enviamos la peticion de inicio de sesion.
                            clientSystem.send(new AccountLoginRequest(email, password));

                        } else if (clientSystem.getState() == MarshalState.FAILED_TO_START) {
                            // Mostramos un mensaje de error.
                            connectionFailed();
                        }
                    }
                }
            }
        }
    }

    private void connectionFailed() {
        Dialog dialog = new Dialog(assetManager.getMessages(Messages.FAILED_TO_CONNECT_TITLE), getSkin());
        dialog.text(assetManager.getMessages(Messages.FAILED_TO_CONNECT_DESCRIPTION));
        dialog.button("OK");
        dialog.show(getStage());
    }
}
