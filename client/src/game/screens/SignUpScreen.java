package game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
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
import shared.network.account.AccountCreationRequest;
import shared.network.account.AccountLoginRequest;
import shared.network.lobby.JoinLobbyRequest;
import shared.util.Messages;

import static game.utils.Resources.CLIENT_CONFIG;

public class SignUpScreen extends AbstractScreen {

    private ClientSystem clientSystem;

    private TextField usernameField;
    private TextField passwordField1, passwordField2;
    private TextField emailField;
    private List<ClientConfiguration.Network.Server> serverList;

    public SignUpScreen(ClientSystem clientSystem) {
        this.clientSystem = clientSystem;
    }

    @Override
    void createContent() {
        ClientConfiguration config = ClientConfiguration.loadConfig(CLIENT_CONFIG); //@todo esto es un hotfix, el config tendría que cargarse en otro lado

        /* Tabla de sign up */
        Window signUpTable = new Window("", getSkin()); //@todo window es una ventana arrastrable
        Label usernameLabel = new Label("Username:", getSkin());
        this.usernameField = new TextField("", getSkin());
        Label emailLabel = new Label("Email:", getSkin());
        this.emailField = new TextField("", getSkin());
        Label passwordLabel1 = new Label("Password:", getSkin());
        this.passwordField1 = new TextField("", getSkin());
        this.passwordField1.setPasswordCharacter('*');
        this.passwordField1.setPasswordMode(true);
        Label passwordLabel2 = new Label("Repeat password:", getSkin());
        this.passwordField2 = new TextField("", getSkin());
        this.passwordField2.setPasswordCharacter('*');
        this.passwordField2.setPasswordMode(true);

        TextButton registerButton = new TextButton("Register account", getSkin());
        registerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (((TextButton)actor).isPressed()) {
                    //@todo validar username, email, password. hashear password.
                    signup();
                }
            }
        });
		
		TextButton goBackButton = new TextButton("Go Back", getSkin());
        goBackButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (((TextButton)actor).isPressed()) {
                    AOGame game = (AOGame) Gdx.app.getApplicationListener();
                    game.toLogin();
                }
            }
        });

        signUpTable.getColor().a = 0.8f;
        signUpTable.add(usernameLabel).padRight(5);
        signUpTable.add(this.usernameField).width(250).row();
        signUpTable.add(emailLabel).padTop(5).padRight(5);
        signUpTable.add(this.emailField).padTop(5).width(250).row();
        signUpTable.add(passwordLabel1).padTop(5).padRight(5);
        signUpTable.add(this.passwordField1).padTop(5).width(250).row();
        signUpTable.add(passwordLabel2).padTop(5).padRight(5);
        signUpTable.add(this.passwordField2).padTop(5).width(250).row();
        signUpTable.add();
        signUpTable.add(registerButton).padTop(20);

        /* Tabla de servidores */
        Table serverTable = new Table((getSkin()));
        this.serverList = new List<>(getSkin());
        this.serverList.setItems(config.getNetwork().getServers());
        serverTable.add(this.serverList).width(400).height(300); //@todo Nota: setear el size acá es redundante, pero si no se hace no se ve bien la lista. Ver (*) más abajo.

        /* Tabla principal */
        getMainTable().add(goBackButton).row();
        getMainTable().add(signUpTable).width(500).height(300).pad(10);
        getMainTable().add(serverTable).width(500).height(300).pad(10);
        getStage().setKeyboardFocus(this.usernameField);
    }

    private void signup() {
        String username = this.usernameField.getText();
        String email = this.emailField.getText();
        String password1 = this.passwordField1.getText();
        String password2 = this.passwordField2.getText();

        if (!password1.equals(password2)) {
            //@todo implement popup
            Dialog dialog = new Dialog("Error", getSkin());
            dialog.text("Las contraseñas no coinciden.");
            dialog.button("OK");
            dialog.show(getStage());
            return;
        }

        /* Conectar el ClientSystem */
        ClientConfiguration.Network.Server server = this.serverList.getSelected();
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
                clientSystem.getKryonetClient().setHost(ip);
                clientSystem.getKryonetClient().setPort(port);

                // Inicializamos la conexion.
                clientSystem.start();

                // Si pudimos conectarnos, mandamos la peticion para loguearnos a la cuenta.
                if (clientSystem.getState() == MarshalState.STARTED) {

                    // Enviamos la peticion de inicio de sesion.
                    clientSystem.getKryonetClient().sendToAll(new AccountCreationRequest(username, email, password1));

                } else if (clientSystem.getState() == MarshalState.FAILED_TO_START) {
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

    @Override
    protected void keyPressed(int keyCode) {
    }
}
