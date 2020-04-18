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
    private ArrayList<Table> userTableArray;
    private ArrayList<Label> nameLabelArray;
    private ArrayList<TextButton> playButtonArray, createButtonArray;

    private Window charSelectWindows, createWindow;
    private TextButton registerButton ;
    private Stack stack;
    private TextField name;
    private SelectBox< Hero > heroSelectBox;

    public CharacterSelectionScreen(){
    }

    public ClientSystem getClientSystem() {
        return clientSystem;
    }

    @Override
    public void createUI() {
        stack = new Stack();
        userTableArray = new ArrayList<>();
        nameLabelArray = new ArrayList<>();
        playButtonArray = new ArrayList<>();
        nameLabelArray = new ArrayList<>();
        createButtonArray = new ArrayList<>();

        charSelectWindows = new Window("", getSkin());
        createWindow = new Window("", getSkin());

        /*ventana de seleccion de personajes*/
        createUsersTable();

        /*ventana de creacion de personajes*/


        Label nameLabel = new Label("Name:", getSkin());
        createWindow.add(nameLabel).row();

        name = new TextField("", getSkin());
        createWindow.add(name).row();

        Label heroLabel = new Label("Hero: ", getSkin());
        createWindow.add(heroLabel).row();

        heroSelectBox = new SelectBox<>(getSkin());
        Array<Hero> heros = new Array<>();
        Hero.getHeroes().forEach(heros::add);
        heroSelectBox.setItems(heros);
        createWindow.add(heroSelectBox).row();

        registerButton = new TextButton("Create", getSkin());

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
            for (int i = 0; i < 6; i++) {
                int index = i;
                if (!userCharacters.get( i ).isBlank()) {
                    charName = userCharacters.get( i );
                    nameLabelArray.get( i ).setText( charName );
                    playButtonArray.get( i ).setDisabled( false );
                    playButtonArray.get( i ).addListener( new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            // send request to create user
                            clientSystem.send( new UserContinueRequest(charName) );
                            playButtonArray.get( index ).setDisabled( true );
                            Timer.schedule( new Timer.Task() {
                                @Override
                                public void run() {
                                    playButtonArray.get( index ).setDisabled( false );
                                }
                            }, 2 );
                        }
                    } );
                }else {
                    playButtonArray.get( i ).setDisabled(true);

                    createButtonArray.get( i ).addListener( new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            toggleWindows();
                            registerButtonListener(index);
                        }
                    } );
                }

            }
        }
    }
    private void createUsersTable(){

        for (int i = 0; i < 6; i++) {
            Table newSlot = new Table();
            newSlot.setBackground( getSkin().getDrawable("menu-frame" ));
            Label userName = new Label("", getSkin());
            TextButton playTextButton = new TextButton( "Jugar",getSkin() );
            TextButton createTextButton = new TextButton( "Crear",getSkin() );
            playButtonArray.add( playTextButton );
            createButtonArray.add( createTextButton );
            userTableArray.add( newSlot );
            nameLabelArray.add( userName );
            userTableArray.get( i ).add( nameLabelArray.get(i)).colspan( 2 ).row();
            userTableArray.get( i ).add().height( 200 ).row();
            userTableArray.get( i ); // HP labels
            userTableArray.get( i ); // Mp labels
            userTableArray.get( i ).add(playButtonArray.get( i )).left().width( 120 ).padLeft( 5 );
            userTableArray.get( i ).add(createButtonArray.get( i )).right().width( 120 ).padRight( 5 );
            charSelectWindows.add( userTableArray.get(i)).width(300).height(300);
            if (i==2){
                charSelectWindows.row();
            }
        }
        charSelectWindows.row();
        TextButton toLoginButton = new TextButton("To Login", getSkin());
        toLoginButton.addListener( new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                screenManager.to(ScreenEnum.LOGIN);
            }
        } );
        charSelectWindows.add(toLoginButton).bottom().right();
    }
    private void registerButtonListener(int index){
        registerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // send request to create user
                clientSystem.send(new UserCreateRequest(name.getText(), heroSelectBox.getSelected().ordinal(),userAcc));
                toggleWindows();
            }
        });
    }
}
