package game.screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import game.systems.network.ClientResponseProcessor;
import game.systems.network.ClientSystem;
import shared.interfaces.Hero;
import shared.network.user.UserContinueRequest;
import shared.network.user.UserCreateRequest;

import java.util.ArrayList;

public class CharacterSelectionScreen extends AbstractScreen {

    private ClientSystem clientSystem;
    private ScreenManager screenManager;
    private ClientResponseProcessor clientResponseProcessor;


    private String charName;
    private String userAcc;
    private ArrayList<String> userCharacters;

    private Window charSelectWindows, createWindow;
    private Table characterTable1;
    private TextButton create, playButton;
    private Label nameLabel1;
    private Stack stack;

    public CharacterSelectionScreen(){
    }

    public ClientSystem getClientSystem() {
        return clientSystem;
    }

    @Override
    public void createUI() {
        stack = new Stack();

        charSelectWindows = new Window("", getSkin());
        createWindow = new Window("", getSkin());
        playButton = new TextButton("Jugar", getSkin());
        playButton.setDisabled( true );
        create = new TextButton("Create", getSkin());
        create.addListener( new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                toggleWindows();
            }
        } );

        TextButton toLoginbutton = new TextButton("To Login", getSkin());
        toLoginbutton.addListener( new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                screenManager.to(ScreenEnum.LOGIN);
            }
        } );


        /*ventana de seleccion de personajes*/

        /* tabla para el primer pj */ // todo hacer 6 iguales
        characterTable1 = new Table();
        characterTable1.setBackground( getSkin().getDrawable( "menu-frame" ));
        nameLabel1 = new Label( "",getSkin() );
        characterTable1.add(nameLabel1).center().colspan( 2 ).row();
        characterTable1.add().height( 200 ).colspan( 2 ).row();//espacio para agregar imagen del pj
        characterTable1.add(); // HP labels
        characterTable1.add().row(); // Mp labels
        characterTable1.add(playButton).bottom().left().width( 140 );
        characterTable1.add(create).bottom().right().width( 140 );
        charSelectWindows.add(characterTable1).width(300).height(300).row();
        charSelectWindows.add(toLoginbutton).bottom().left().pad( 10 );

        /*ventana de creacion de personajes*/


        Label nameLabel = new Label("Name:", getSkin());
        createWindow.add(nameLabel).row();

        TextField name = new TextField("", getSkin());
        createWindow.add(name).row();

        Label heroLabel = new Label("Hero: ", getSkin());
        createWindow.add(heroLabel).row();

        SelectBox< Hero > heroSelectBox = new SelectBox<>(getSkin());
        Array<Hero> heros = new Array<>();
        Hero.getHeroes().forEach(heros::add);
        heroSelectBox.setItems(heros);
        createWindow.add(heroSelectBox).row();

        TextButton registerButton = new TextButton("Create", getSkin());
        registerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // send request to create user
                clientSystem.send(new UserCreateRequest(name.getText(), heroSelectBox.getSelected().ordinal(),userAcc));
                toggleWindows();
            }
        });
        createWindow.add(registerButton).row();

        TextButton goBackButton = new TextButton("Go Back", getSkin());
        goBackButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                toggleWindows();
            }
        });
        createWindow.add(goBackButton);

        stack.add( charSelectWindows );
        stack.add( createWindow );
        createWindow.setVisible( false );
        charSelectWindows.setVisible( true );
        getMainTable().add(stack).fill().pad( 10 );

    }

    private void toggleWindows(){
        createWindow.setVisible(!createWindow.isVisible());
        charSelectWindows.setVisible(!charSelectWindows.isVisible());
    }

    public void setUserCharacters(ArrayList< String > userCharacters) {
        this.userCharacters = userCharacters;
    }

    public void setUserAcc(String userAcc) {
        this.userAcc = userAcc;
    }

    public void windowsUpdate(){
        if (!userCharacters.isEmpty()) {
            charName = userCharacters.get( 0 );
            nameLabel1.setText(charName);
            playButton.setDisabled( false );
            playButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    // send request to create user
                    clientSystem.send(new UserContinueRequest(charName));
                    playButton.setDisabled(true);
                    Timer.schedule( new Timer.Task() {
                        @Override
                        public void run() {
                            playButton.setDisabled(false);
                        }
                    }, 2);
                }
            });

        } else {
            characterTable1.add(create);
        }

    }
}
