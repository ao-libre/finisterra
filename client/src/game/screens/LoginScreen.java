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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import game.ClientConfiguration;
import game.ClientConfiguration.Network.Server;
import game.handlers.DefaultAOAssetManager;
import game.systems.network.ClientSystem;
import game.systems.resources.MusicSystem;
import game.systems.resources.SoundsSystem;
import game.ui.ExtendedDialog;
import game.ui.WidgetFactory;
import game.ui.WidgetFactory.Drawables;
import shared.network.account.AccountLoginRequest;
import shared.util.Messages;

@Wire
public class LoginScreen extends AbstractScreen {

    private final Preferences preferences = Gdx.app.getPreferences("Finisterra");
    @Wire private DefaultAOAssetManager assetManager;
    @Wire private MusicSystem musicSystem;
    private ClientConfiguration clientConfiguration;
    private ClientSystem clientSystem;
    private ScreenManager screenManager;
    private SoundsSystem soundsSystem;
    private TextField emailField;
    private TextField passwordField;
    private CheckBox rememberMe;
    private CheckBox seePassword;
    private CheckBox disableMusic;
    private CheckBox disableSound;
    private TextButton loginButton;
    private List<ClientConfiguration.Network.Server> serverList;
    private boolean isDialogShowed = false;

    public LoginScreen() {
    }

    @Override
    protected void keyPressed(int keyCode) {
        if (keyCode == Input.Keys.ESCAPE) {
            // arregla bug en el que se sigen generando dialog si se apreta multiple veces ESCAPE
            if (!isDialogShowed) {
                isDialogShowed = true;
                ExtendedDialog dialog = new ExtendedDialog( "Cerrar juego", getSkin() );
                dialog.text( "¿Está seguro que desea cerrar el juego?" );
                dialog.button( "Aceptar", Gdx.app::exit );
                dialog.button( "Cancelar", () -> {isDialogShowed = false;});
                dialog.show( getStage() );
            }
        }
    }

    @Override
    protected void createUI() {
        ClientConfiguration.Account account = clientConfiguration.getAccount();

        /* Tabla de login */
        Window loginWindow = WidgetFactory.createWindow();
        loginWindow.getTitleLabel().setAlignment( Align.center );
        loginWindow.getTitleLabel().setText( "Login Windows" );
        Label emailLabel = WidgetFactory.createLabel("Email: ");
        //tamaño de las fuentes (nota soy chicaton :P )
        emailLabel.getStyle().font = getSkin().getFont( "big" );
        emailField = WidgetFactory.createTextField(account.getEmail());
        Label passwordLabel = WidgetFactory.createLabel("Password");
        passwordLabel.getStyle().font = getSkin().getFont( "big" );
        passwordField = WidgetFactory.createTextField(account.getPassword());
        passwordField.setPasswordCharacter('*');
        passwordField.setPasswordMode(true);
        rememberMe = WidgetFactory.createCheckBox("Remember me");
        if (preferences.getBoolean("rememberMe")) {
            rememberMe.setChecked(true);
            if (emailField.getText().isEmpty()) {
                if (!preferences.getString("userEmail").isEmpty()) {
                    emailField.setText(preferences.getString("userEmail"));
                }
            }
        }
        seePassword = WidgetFactory.createCheckBox("See Password");
        seePassword.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                passwordField.setPasswordMode(!passwordField.isPasswordMode());
            }
        });

        loginButton = WidgetFactory.createTextButton("Login");
        loginButton.addListener(new LoginButtonListener());

        TextButton newAccountButton = WidgetFactory.createTextButton("New account");
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

        /* Tabla de servidores
        * reemplazado por windows
        * */
        Window connectionTable = WidgetFactory.createWindow();
        // agrege un titulo a la lista de servidores
        connectionTable.getTitleLabel().setText( "Server List" );
        connectionTable.getTitleLabel().setAlignment( Align.center );
        serverList = WidgetFactory.createList();
        serverList.setAlignment( Align.center );
        serverList.setItems(clientConfiguration.getNetwork().getServers());
        serverList.getStyle().font = getSkin().getFont( "big" );
        // panel desplasable
        // las barra de desplasamiento aparece cuando la lista sobrepasa el tamaño
        ScrollPane scrollPane = WidgetFactory.createScrollPane(serverList,false,true,false,true);
        // transparencia para igualar la ventana de login
        connectionTable.getColor().a = 0.8f;
        // Nota: setear el size acá es redundante, pero si no se hace no se ve bien la lista. Ver (*) más abajo.
        connectionTable.add(scrollPane).colspan(2).width(350).height(250);
        connectionTable.row();

        TextButton addServerButton = WidgetFactory.createTextButton("Añadir servidor");
        addServerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ExtendedDialog dialog = new ExtendedDialog("Añadir servidor", getSkin());
                TextField serverNameField = WidgetFactory.createTextField("Server Name");
                serverNameField.setMaxLength( 20 );
                TextField ipField = WidgetFactory.createTextField("127.0.0.1");
                TextField portField = WidgetFactory.createTextField("7666");
                dialog.getContentTable().add(WidgetFactory.createLabel("SERVER NAME: "));
                dialog.getContentTable().add(serverNameField).row();
                dialog.getContentTable().add(WidgetFactory.createLabel("IP: "));
                dialog.getContentTable().add(ipField).row();
                dialog.getContentTable().add(WidgetFactory.createLabel("PORT: "));
                dialog.getContentTable().add(portField).row();
                dialog.button("Aceptar", () -> {
                    String name = serverNameField.getText();
                    String ip = ipField.getText();
                    int port;
                    try {
                        port = Integer.parseInt(portField.getText());
                    } catch (NumberFormatException ignored) {
                        return;
                    }
                    // chequeo servidor esta en la lista
                    String newSever = name + "  " + ip + ":" + port;
                    Array<Server> servers = clientConfiguration.getNetwork().getServers();
                    boolean serverExist = false;

                    for (int i = 0; i < servers.size; i++) {
                        if (servers.get(i).toString().equals(newSever)){
                            serverExist = true;
                            break;
                        }
                    }
                    /*
                     * si esta en la lista lanza cuadro de error si no agrega el servidor
                     * todo guardar la lista de servidores
                     */
                    if (serverExist){
                        ExtendedDialog dialog1 = new ExtendedDialog("Error", getSkin());
                        dialog1.getTitleLabel().setAlignment( Align.center );
                        dialog1.text("El servidor ya esta en la lista\n" +
                                "The sever is already in the list");
                        dialog1.button("ok");
                        dialog1.show(getStage());
                    }
                    else {
                        clientConfiguration.getNetwork().getServers().add( new Server( name, ip, port ) );
                        serverList.setItems(clientConfiguration.getNetwork().getServers());
                    }
                });
                dialog.button( "Cancel" );
                dialog.show(getStage());
            }
        });
        connectionTable.add(addServerButton);

        TextButton deleteServerButton = WidgetFactory.createTextButton("Eliminar servidor");
        deleteServerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                clientConfiguration.getNetwork().getServers().removeValue(serverList.getSelected(), true);
                serverList.setItems(clientConfiguration.getNetwork().getServers());
            }
        });
        connectionTable.add(deleteServerButton);

        /* Botones para desactivar el sonido y la musica*/

        /* Musica */
        disableMusic = new CheckBox("Desabilitar Musica", getSkin());
        if (preferences.getBoolean("MusicOff")) {
            disableMusic.setChecked(true);
            musicSystem.setDisabled(true);
        }

        disableMusic.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                musicSystem.setDisabled(!musicSystem.isDisabled());
                preferences.putBoolean("MusicOff", disableMusic.isChecked());
                preferences.flush();
            }
        });

        Slider musicVolumeBar = new Slider(0.0f, 1.0f, 0.1f, false, getSkin());
        musicVolumeBar.setValue(musicSystem.getVolume());
        musicVolumeBar.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                musicSystem.setVolume(musicVolumeBar.getValue());
            }
        });

        /* Sonido */
        disableSound = new CheckBox("Desabilitar sonido", getSkin());
        if (preferences.getBoolean("SoundOff")) {
            disableSound.setChecked(true);
            soundsSystem.setDisabled(true);
        }
        disableSound.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                preferences.putBoolean("SoundOff", disableSound.isChecked());
                preferences.flush();
                soundsSystem.setDisabled(!soundsSystem.isDisabled());
            }
        });

        Slider soundVolumeBar = new Slider(0.0f, 1.0f, 0.1f, false, getSkin());
        soundVolumeBar.setValue(soundsSystem.getVolume());
        soundVolumeBar.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundsSystem.setVolume(musicVolumeBar.getValue());
            }
        });

        /* Agrega la imagen del logo */
        Cell<Image> logoCell = getMainTable().add(WidgetFactory.createImage(new Texture(Gdx.files.local("data/ui/images/logo-big.png"))))
                .pad(20).center();
        logoCell.row();

        /* Tabla botones */
        Window buttonsTable = WidgetFactory.createWindow();
        // transparencia para igualar la ventana de loggin
        buttonsTable.getColor().a = 0.8f;;
        buttonsTable.setMovable(false);
        buttonsTable.background(WidgetFactory.createDrawable(Drawables.SLOT.name));
        buttonsTable.getTitleLabel().setColor(Color.GOLD);
        buttonsTable.getTitleLabel().setAlignment(2);
        buttonsTable.setHeight(110);
        buttonsTable.add(disableMusic).width(500).pad(5);
        buttonsTable.add(disableSound).width(400).pad(5);
        buttonsTable.row();
        buttonsTable.add(musicVolumeBar);
        buttonsTable.add(soundVolumeBar);

        /* Tabla para loguin y servers */
        Table login_server = new Table();
        login_server.add(loginWindow).width(500).height(400).padLeft(10).padRight(10).padTop(10);
        //(*) Seteando acá el size, recursivamente tendría que resizear list.
        login_server.add(connectionTable).width(400).height(400).padLeft(10).padRight(10).padTop(10);

        /* Tabla principal */
        getMainTable().add(login_server).row();
        getMainTable().add(buttonsTable).height(150).width(920).pad(3);
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
            if (rememberMe.isChecked()) {
                preferences.putString("userEmail", emailField.getText());
                preferences.putBoolean("rememberMe", true);
            } else {
                preferences.remove("userEmail");
                preferences.putBoolean("rememberMe", false);
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

            // Si podemos conectarnos, mandamos la peticion para loguearnos a la cuenta.
            if (clientSystem.connect(ip, port)) {
                clientSystem.send(new AccountLoginRequest(email, password));
            } else {
                screenManager.showDialog(
                        assetManager.getMessages(Messages.FAILED_TO_CONNECT_TITLE),
                        assetManager.getMessages(Messages.FAILED_TO_CONNECT_DESCRIPTION)
                );
            }
        }
    }
}
