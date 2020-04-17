package game.screens;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Timer;
import game.ClientConfiguration;
import game.handlers.DefaultAOAssetManager;
import game.systems.network.ClientSystem;
import game.systems.resources.MusicSystem;
import game.systems.resources.SoundsSystem;
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
    private MusicSystem musicSystem;
    private SoundsSystem soundsSystem;

    private TextField emailField;
    private TextField passwordField;
    private CheckBox rememberMe; //@todo implementar remember me
    private CheckBox seePassword;
    private CheckBox disableMusic;
    private CheckBox disableSound;
    private TextButton loginButton;
    private List<ClientConfiguration.Network.Server> serverList;
    private Preferences preferences = Gdx.app.getPreferences("Finisterra");;

    public LoginScreen() {
    }

    @Override
    protected void keyPressed(int keyCode) {
        if (keyCode == Input.Keys.ESCAPE) {
            Gdx.app.exit();
        }
//       if (keyCode == Input.Keys.ENTER && this.canConnect) {
//           this.canConnect = false;
//           connectThenLogin();
//           Gdx.app.exit();
//       }
    }

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
        if(preferences.getBoolean( "rememberMe" )){
            rememberMe.setChecked( true );
            if (emailField.getText().isBlank()) {
                if(!preferences.getString( "userEmail" ).isBlank()){
                    emailField.setText( preferences.getString( "userEmail" ) );
                }
            }
        }
        seePassword = new CheckBox("See Password", getSkin());
        seePassword.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                passwordField.setPasswordMode(!passwordField.isPasswordMode());
            }
        });

        loginButton = new TextButton("Login", getSkin());
        loginButton.addListener(new LoginButtonListener());

        TextButton newAccountButton = new TextButton("New account", getSkin());
        newAccountButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                screenManager.to(ScreenEnum.SIGN_UP);
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

        /* Botones para desactivar el sonido y la musica*/

        /* Musica */
        disableMusic = new CheckBox( "Desabilitar Musica",getSkin() );
        if(preferences.getBoolean( "MusicOff" )){
            disableMusic.setChecked( true );
            musicSystem.stopMusic();
            musicSystem.setDisableMusic( true );
        }

        disableMusic.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                musicSystem.setDisableMusic(!musicSystem.isDisableMusic());
                preferences.putBoolean( "MusicOff", disableMusic.isChecked() );
                preferences.flush();
                if (!musicSystem.isDisableMusic()){
                    musicSystem.playMusic( 101 );
                    musicSystem.fadeInMusic( 1f,20f );
                }else{
                    musicSystem.stopMusic();
                }
            }
        });

        /* Sonido */
        disableSound = new CheckBox( "Desabilitar sonido",getSkin() );
        if(preferences.getBoolean( "SoundOff" )){
            disableSound.setChecked( true );
            soundsSystem.setDisableSounds( true );
        }
        disableSound.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                preferences.putBoolean( "SoundOff", disableSound.isChecked() );
                preferences.flush();
                soundsSystem.setDisableSounds(!soundsSystem.isDisableSounds());
            }
        });

        /* Agrega la imagen del logo */
        Cell<Image> logoCell = getMainTable().add(new Image( new Texture( Gdx.files.local("data/ui/images/logo-big.png")))).center();
        logoCell.row();

        /* Tabla botones */
        Window buttonsTable = new Window("OPCIONES", getSkin());
        buttonsTable.setMovable( false );
        buttonsTable.background( getSkin().getDrawable("menu-frame"));
        buttonsTable.getTitleLabel().setColor( Color.GOLD );
        buttonsTable.getTitleLabel().setAlignment( 2 );
        buttonsTable.setHeight( 100 );
        buttonsTable.add(disableMusic).width(500).pad(10);
        buttonsTable.add(disableSound).width(400).pad(10);

        /* Tabla para loguin y servers */
        Table login_server = new Table();
        login_server.add(loginWindow).width(500).height(300).padLeft(10).padRight( 10 ).padTop( 10 );
        login_server.add(connectionTable).width(400).height(300).padLeft(10).padRight( 10 ).padTop( 10 ); //(*) Seteando acá el size, recursivamente tendría que resizear list.

        /* Tabla principal */
        getMainTable().add(login_server).row();
        getMainTable().add(buttonsTable).height( 100 ).width( 920 ).pad(3);
        getStage().setKeyboardFocus(emailField);
    }

    private class LoginButtonListener extends ChangeListener {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
            // El botón fue apretado
            loginButton.setDisabled(true);
            Timer.schedule(new Timer.Task() { //@todo implementar API que tome lambdas () -> {}
                @Override
                public void run() {
                    loginButton.setDisabled(false);
                }
            }, 2);
            if (rememberMe.isChecked()){
                preferences.putString("userEmail",emailField.getText());
                preferences.putBoolean("rememberMe",true);
            } else {
                preferences.remove( "userEmail" );
                preferences.putBoolean("rememberMe",false);
            }
            preferences.flush();

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

    private void connectionFailed() {
        Dialog dialog = new Dialog(assetManager.getMessages(Messages.FAILED_TO_CONNECT_TITLE), getSkin());
        dialog.text(assetManager.getMessages(Messages.FAILED_TO_CONNECT_DESCRIPTION));
        dialog.button("OK");
        dialog.show(getStage());
    }
}
