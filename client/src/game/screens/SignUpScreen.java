package game.screens;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Timer;
import game.ClientConfiguration;
import game.handlers.DefaultAOAssetManager;
import game.systems.network.ClientSystem;
import game.ui.WidgetFactory;
import shared.network.account.AccountCreationRequest;
import shared.util.Messages;

@Wire
public class SignUpScreen extends AbstractScreen {

    @Wire
    private DefaultAOAssetManager assetManager;
    private ClientConfiguration clientConfiguration;
    private ClientSystem clientSystem;
    private ScreenManager screenManager;

    private TextField usernameField;
    private TextField passwordField1, passwordField2;
    private TextField emailField;
    private TextButton registerButton;
    private List<ClientConfiguration.Network.Server> serverList;

    @Override
    protected void createUI() {
        /* Tabla de sign up */
        Window signUpTable = WidgetFactory.createWindow(); //@todo window es una ventana arrastrable
        Label usernameLabel = WidgetFactory.createLabel("Username: ");
        usernameField = WidgetFactory.createTextField("");
        Label emailLabel = WidgetFactory.createLabel("Email:");
        emailField = WidgetFactory.createTextField("");
        Label passwordLabel1 = WidgetFactory.createLabel("Password:");
        passwordField1 = WidgetFactory.createTextField("");
        passwordField1.setPasswordCharacter('*');
        passwordField1.setPasswordMode(true);
        Label passwordLabel2 = WidgetFactory.createLabel("Repeat password:");
        passwordField2 = WidgetFactory.createTextField("");
        passwordField2.setPasswordCharacter('*');
        passwordField2.setPasswordMode(true);

        registerButton = WidgetFactory.createTextButton("Register account");
        registerButton.addListener(new RegisterButtonListener());

        TextButton goBackButton = WidgetFactory.createTextButton("Go Back");
        goBackButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                screenManager.to(ScreenEnum.LOGIN);
            }
        });

        signUpTable.getColor().a = 0.8f;
        signUpTable.add(usernameLabel).padRight(5);
        signUpTable.add(usernameField).width(250).row();
        signUpTable.add(emailLabel).padTop(5).padRight(5);
        signUpTable.add(emailField).padTop(5).width(250).row();
        signUpTable.add(passwordLabel1).padTop(5).padRight(5);
        signUpTable.add(passwordField1).padTop(5).width(250).row();
        signUpTable.add(passwordLabel2).padTop(5).padRight(5);
        signUpTable.add(passwordField2).padTop(5).width(250).row();
        signUpTable.add();
        signUpTable.add(registerButton).padTop(20);

        /* Tabla de servidores */
        Table serverTable = new Table((getSkin()));
        serverList = WidgetFactory.createList();
        serverList.setItems(clientConfiguration.getNetwork().getServers());
        serverTable.add(serverList).width(400).height(300); //@todo Nota: setear el size acá es redundante, pero si no se hace no se ve bien la lista. Ver (*) más abajo.

        /* Tabla principal */
        getMainTable().add(goBackButton).row();
        getMainTable().add(signUpTable).width(500).height(300).pad(10);
        getMainTable().add(serverTable).width(400).height(300).pad(10);
        getStage().setKeyboardFocus(usernameField);
    }

    private void connectionFailed() {
        // Mostramos un mensaje de error.
        Dialog dialog = new Dialog(assetManager.getMessages(Messages.FAILED_TO_CONNECT_TITLE), getSkin());
        dialog.text(assetManager.getMessages(Messages.FAILED_TO_CONNECT_DESCRIPTION));
        dialog.button("OK");
        dialog.show(getStage());
    }

    //Listener para registerButton
    private class RegisterButtonListener extends ChangeListener {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
            // El botón fue apretado
            registerButton.setDisabled(true);
            Timer.schedule(new Timer.Task() { //@todo implementar API que tome lambdas () -> {}
                @Override
                public void run() {
                    registerButton.setDisabled(false);
                }
            }, 2);

            String username = usernameField.getText();
            String email = emailField.getText();
            String password1 = passwordField1.getText();
            String password2 = passwordField2.getText();

            if (!password1.equals(password2)) {
                Dialog dialog = new Dialog("Error", getSkin());
                dialog.text("Las contraseñas no coinciden.");
                dialog.button("OK");
                dialog.show(getStage());
                return;
            }

            // Conectar el ClientSystem
            ClientConfiguration.Network.Server server = serverList.getSelected();
            if (server == null) return;
            String ip = server.getHostname();
            int port = server.getPort();

            // Si podemos conectarnos, mandamos la peticion para crear a la cuenta.
            if (clientSystem.connect(ip, port)) {
                clientSystem.send(new AccountCreationRequest(username, email, password1));
            } else {
                connectionFailed();
            }
        }
    }
}
