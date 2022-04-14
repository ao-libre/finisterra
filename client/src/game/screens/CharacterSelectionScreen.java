package game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.esotericsoftware.minlog.Log;
import game.systems.network.ClientResponseProcessor;
import game.systems.network.ClientSystem;
import game.ui.WidgetFactory;
import shared.interfaces.Hero;
import shared.network.user.UserCreateRequest;
import shared.network.user.UserLoginRequest;

import java.util.ArrayList;

public class CharacterSelectionScreen extends AbstractScreen {

    private final Texture noHero = new Texture(Gdx.files.local("data/ui/images/pj/noHero.jpg")),
            warriorImage = new Texture(Gdx.files.local("data/ui/images/pj/guerrero.jpg")),
            mageImage = new Texture(Gdx.files.local("data/ui/images/pj/mago.jpg")),
            assassinImage = new Texture(Gdx.files.local("data/ui/images/pj/asesino.jpg")),
            paladinImage = new Texture(Gdx.files.local("data/ui/images/pj/paladin.jpg")),
            bardImage = new Texture(Gdx.files.local("data/ui/images/pj/bardo.jpg")),
            archerImage = new Texture(Gdx.files.local("data/ui/images/pj/cazador.jpg")),
            clericImage = new Texture(Gdx.files.local("data/ui/images/pj/clerigo.jpg"));
    private ClientSystem clientSystem;
    private ScreenManager screenManager;
    private ClientResponseProcessor clientResponseProcessor;
    private String charName;
    private String userAcc;
    private ArrayList<String> userCharacters;
    private ArrayList<Integer> userCharactersData;
    private ArrayList<Table> userTableArray, userImageTableArray;
    private ArrayList<Label> nameLabelArray, HPLabelArray, MPLabelArray;
    private ArrayList<TextButton> playButtonArray, createButtonArray;
    private Image heroSelectionImage;
    private Window charSelectWindows, createWindow;
    private TextButton registerButton;
    private Stack stack;
    private TextField name;
    private SelectBox<Hero> heroSelectBox;

    public CharacterSelectionScreen() {
    }

    public ClientSystem getClientSystem() {
        return clientSystem;
    }

    @Override
    public void createUI() {
        stack = new Stack();
        userTableArray = new ArrayList<>();
        nameLabelArray = new ArrayList<>();
        createButtonArray = new ArrayList<>();
        HPLabelArray = new ArrayList<>();
        MPLabelArray = new ArrayList<>();
        userImageTableArray = new ArrayList<>();
        playButtonArray = playButtonArray != null ? playButtonArray : new ArrayList<>();

        charSelectWindows = WidgetFactory.createWindow();
        createWindow = WidgetFactory.createWindow();

        /*ventana de seleccion de personajes*/
        createUsersTable();

        /*ventana de creacion de personajes*/
        Table heroImageTable = new Table();
        heroSelectionImage = WidgetFactory.createImage(warriorImage);
        heroImageTable.clear();
        heroImageTable.add(heroSelectionImage);
        createWindow.add(heroImageTable).height(200).row();

        Label nameLabel = WidgetFactory.createLabel("Name:");
        createWindow.add(nameLabel).row();

        name = WidgetFactory.createTextField("");
        createWindow.add(name).row();

        Label heroLabel = WidgetFactory.createLabel("Hero: ");
        createWindow.add(heroLabel).row();
        heroSelectBox = WidgetFactory.createSelectBox();
        Array<Hero> heros = new Array<>();
        Hero.getHeroes().forEach(heros::add);
        heroSelectBox.setItems(heros);
        heroSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                heroImageTable.clear();
                switch (heroSelectBox.getSelected()) {
                    case GUERRERO:
                        heroSelectionImage = WidgetFactory.createImage(warriorImage);
                        break;
                    case MAGO:
                        heroSelectionImage = WidgetFactory.createImage(mageImage);
                        break;
                    case ASESINO:
                        heroSelectionImage = WidgetFactory.createImage(assassinImage);
                        break;
                    case PALADIN:
                        heroSelectionImage = WidgetFactory.createImage(paladinImage);
                        break;
                    case BARDO:
                        heroSelectionImage = WidgetFactory.createImage(bardImage);
                        break;
                    case ARQUERO:
                        heroSelectionImage = WidgetFactory.createImage(archerImage);
                        break;
                    case CLERIGO:
                        heroSelectionImage = WidgetFactory.createImage(clericImage);
                        break;
                    // todo: caso default, assert, etc.
                }
                heroImageTable.add(heroSelectionImage);
            }
        });
        createWindow.add(heroSelectBox).row();

        registerButton = WidgetFactory.createTextButton("Create");

        createWindow.add(registerButton).row();

        TextButton goBackButton = WidgetFactory.createTextButton("Go Back");
        goBackButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                toggleWindows();
            }
        });
        createWindow.add(goBackButton);

        stack.add(charSelectWindows);
        stack.add(createWindow);
        createWindow.setVisible(false);
        charSelectWindows.setVisible(true);
        getMainTable().add(stack).fill().pad(10);

    }

    private void toggleWindows() {
        createWindow.setVisible(!createWindow.isVisible());
        charSelectWindows.setVisible(!charSelectWindows.isVisible());
    }

    public void setUserCharacters(ArrayList<String> userCharacters) {
        this.userCharacters = userCharacters;
    }

    public void setUserCharactersData(ArrayList<Integer> userCharactersData) {
        this.userCharactersData = userCharactersData;
    }

    public void setUserAcc(String userAcc) {
        this.userAcc = userAcc;
    }

    public void windowsUpdate() {
        Log.info("" + userCharactersData);
        if (!userCharacters.isEmpty()) {
            for (int i = 0; i < 6; i++) {
                int index = i;
                // todo: Implementar String::isBlank() de Java 11 en algún módulo de utilidades
                if (!userCharacters.get(i).chars().allMatch(Character::isWhitespace)) {
                    charName = userCharacters.get(i);
                    int userHeroID = userCharactersData.get(i);

                    switch (userHeroID) {
                        case -1:
                            playButtonArray.get(i).setDisabled(true);
                            break;
                        case 0:
                            userImageTableArray.get(i).clearChildren();
                            userImageTableArray.get(i).add(WidgetFactory.createImage(warriorImage));
                            break;
                        case 1:
                            userImageTableArray.get(i).clearChildren();
                            userImageTableArray.get(i).add(WidgetFactory.createImage(mageImage));
                            break;
                        case 2:
                            userImageTableArray.get(i).clearChildren();
                            userImageTableArray.get(i).add(WidgetFactory.createImage(assassinImage));
                            break;
                        case 3:
                            userImageTableArray.get(i).clearChildren();
                            userImageTableArray.get(i).add(WidgetFactory.createImage(paladinImage));
                            break;
                        case 4:
                            userImageTableArray.get(i).clearChildren();
                            userImageTableArray.get(i).add(WidgetFactory.createImage(bardImage));
                            break;
                        case 5:
                            userImageTableArray.get(i).clearChildren();
                            userImageTableArray.get(i).add(WidgetFactory.createImage(archerImage));
                            break;
                        case 6:
                            userImageTableArray.get(i).clearChildren();
                            userImageTableArray.get(i).add(WidgetFactory.createImage(clericImage));
                            break;
                    }
                    HPLabelArray.get(i).setText(userCharactersData.get(i + 6) + " / " + userCharactersData.get(i + 12));
                    MPLabelArray.get(i).setText(userCharactersData.get(i + 18) + " / " + userCharactersData.get(i + 24));
                    nameLabelArray.get(i).setText(charName);
                    if (userHeroID != -1) {
                        playButtonArray.get(i).setDisabled(false);
                        playButtonListener(i, charName);
                    }
                } else {
                    playButtonArray.get(i).setDisabled(true);
                }
                createButtonArray.get(i).addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        toggleWindows();
                        registerButtonListener(index);
                    }
                });
            }
        }
    }

    private void createUsersTable() {

        for (int i = 0; i < 6; i++) {
            Table newSlot = new Table();
            newSlot.setBackground(getSkin().getDrawable("menu-frame"));
            Table imageTable = new Table();
            Image pjImage = WidgetFactory.createImage(noHero);
            imageTable.add(pjImage);
            Label userName = WidgetFactory.createLabel("");
            Label HPLabel = WidgetFactory.createLabel("????/????");
            HPLabel.setColor(Color.RED);
            Label MPLabel = WidgetFactory.createLabel("????/????");
            MPLabel.setColor(Color.BLUE);
            TextButton playTextButton = WidgetFactory.createTextButton("Jugar");
            TextButton createTextButton = WidgetFactory.createTextButton("Crear");


            playButtonArray.add(playTextButton);
            createButtonArray.add(createTextButton);
            userTableArray.add(newSlot);
            userImageTableArray.add(imageTable);
            nameLabelArray.add(userName);
            HPLabelArray.add(HPLabel);
            MPLabelArray.add(MPLabel);

            /*dibujar la tablas*/
            userTableArray.get(i).add(nameLabelArray.get(i)).colspan(2).row();
            userTableArray.get(i).add(userImageTableArray.get(i)).height(200).colspan(2).row();
            userTableArray.get(i).add(HPLabelArray.get(i)).padRight(10).padLeft(10);
            userTableArray.get(i).add(MPLabelArray.get(i)).padRight(10).padLeft(10).row();
            userTableArray.get(i).add(playButtonArray.get(i)).left().width(120).padLeft(5);
            userTableArray.get(i).add(createButtonArray.get(i)).right().width(120).padRight(5);
            charSelectWindows.add(userTableArray.get(i)).width(300).height(300);
            if (i == 2) {
                charSelectWindows.row();
            }
        }
        charSelectWindows.row();
        TextButton toLoginButton = WidgetFactory.createTextButton("To Login");
        toLoginButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                clientSystem.stop();
                screenManager.to(ScreenEnum.LOGIN);
            }
        });
        charSelectWindows.add(toLoginButton).bottom().right();
    }

    private void registerButtonListener(int index) {
        registerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (name.getText().chars().allMatch(Character::isWhitespace)) {
                    Dialog dialog = new Dialog("Error", getSkin());
                    dialog.add("El nombre no puede estar en blanco");
                    dialog.button("OK");
                    dialog.show(screenManager.getAbstractScreen().getStage());
                } else {
                    // send request to create user
                    clientSystem.send(new UserCreateRequest(name.getText(), heroSelectBox.getSelected().ordinal(), userAcc, index));
                    registerButton.setDisabled(true);
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            registerButton.setDisabled(false);
                        }
                    }, 2);
                }
            }
        });
    }

    private void playButtonListener(int index, String userName) {
        playButtonArray.get(index).addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // deshabilitamos los botones temporalmente
                for (TextButton playButton : playButtonArray) {
                    if (!playButton.isDisabled()) {
                        playButton.setDisabled(true);
                        Timer.schedule(new Timer.Task() {
                            @Override
                            public void run() {
                                playButton.setDisabled(false);
                            }
                        }, 3);
                    }
                }
                // send request to login user
                clientSystem.send(new UserLoginRequest(userName));
            }
        });
    }
}
