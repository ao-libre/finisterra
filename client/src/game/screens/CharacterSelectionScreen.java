package game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import game.systems.network.ClientResponseProcessor;
import game.systems.network.ClientSystem;
import game.ui.WidgetFactory;
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
    private ArrayList<Label> nameLabelArray, HPLabelArray, MPLabelArray;
    private ArrayList<TextButton> playButtonArray, createButtonArray;
    private ArrayList<Boolean> booleanArrayList;
    private ArrayList<Image> pjImages;
    private final Texture noHero = new Texture(Gdx.files.local("data/ui/images/pj/noHero.jpg")),
            warriorImage = new Texture(Gdx.files.local("data/ui/images/pj/guerrero.jpg")),
            mageImage = new Texture(Gdx.files.local("data/ui/images/pj/mago.jpg")),
            assassinImage = new Texture(Gdx.files.local("data/ui/images/pj/asesino.jpg")),
            paladinImage = new Texture(Gdx.files.local("data/ui/images/pj/paladin.jpg")),
            bardImage = new Texture(Gdx.files.local("data/ui/images/pj/bardo.jpg")),
            archerImage = new Texture(Gdx.files.local("data/ui/images/pj/cazador.jpg")),
            clericImage = new Texture(Gdx.files.local("data/ui/images/pj/clerigo.jpg"));

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
        playButtonArray = new ArrayList<>();
        nameLabelArray = new ArrayList<>();
        createButtonArray = new ArrayList<>();
        HPLabelArray = new ArrayList<>();
        MPLabelArray = new ArrayList<>();
        booleanArrayList = new ArrayList<>();
        pjImages = new ArrayList<>();

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

    public void setUserAcc(String userAcc) {
        this.userAcc = userAcc;
    }

    public void windowsUpdate() {

        if (!userCharacters.isEmpty()) {
            for (int i = 0; i < 6; i++) {
                int index = i;
                if (!userCharacters.get(i).isBlank()) {
                    charName = userCharacters.get(i);
                    nameLabelArray.get(i).setText(charName);
                    playButtonArray.get(i).setDisabled(false);
                    playButtonListener(i, charName);

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
            Label userName = WidgetFactory.createLabel("");
            Image pjImage = WidgetFactory.createImage(noHero);
            Label HPLabel = WidgetFactory.createLabel("????/????");
            HPLabel.setColor(Color.RED);
            Label MPLabel = WidgetFactory.createLabel("????/????");
            MPLabel.setColor(Color.BLUE);
            TextButton playTextButton = WidgetFactory.createTextButton("Jugar");
            TextButton createTextButton = WidgetFactory.createTextButton("Crear");

            playButtonArray.add(playTextButton);
            createButtonArray.add(createTextButton);
            userTableArray.add(newSlot);
            nameLabelArray.add(userName);
            HPLabelArray.add(HPLabel);
            MPLabelArray.add(MPLabel);
            pjImages.add(pjImage);

            /*dibujar la tablas*/
            userTableArray.get(i).add(nameLabelArray.get(i)).colspan(2).row();
            userTableArray.get(i).add(pjImages.get(i)).height(200).colspan(2).row();
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
                screenManager.to(ScreenEnum.LOGIN);
            }
        });
        charSelectWindows.add(toLoginButton).bottom().right();
    }

    private void registerButtonListener(int index) {
        registerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (name.getText().isBlank()) {
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
                // send request to login user
                clientSystem.send(new UserContinueRequest(userName));
                for (int x = 0; x < 6; x++) {
                    booleanArrayList.add(x, playButtonArray.get(x).isDisabled());
                    playButtonArray.get(x).setDisabled(true);
                }
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        for (int y = 0; y < 6; y++) {
                            playButtonArray.get(y).setDisabled(booleanArrayList.get(y));
                        }
                    }
                }, 2);
            }
        });
    }
}
