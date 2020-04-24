package game.screens;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import game.systems.network.ClientSystem;
import game.ui.WidgetFactory;
import shared.interfaces.Hero;

@Wire
public class CreateScreen extends AbstractScreen {
    private ClientSystem clientSystem;
    private ScreenManager screenManager;

    public CreateScreen() {
    }

    @Override
    protected void keyPressed(int keyCode) {

    }

    public ClientSystem getClientSystem() {
        return clientSystem;
    }

    @Override
    public Skin getSkin() {
        return super.getSkin();
    }

    @Override
    public void createUI() {
        Window createWindow = WidgetFactory.createWindow();

        Label nameLabel = WidgetFactory.createLabel("Name:");
        createWindow.add(nameLabel).row();

        TextField name = WidgetFactory.createTextField("");
        createWindow.add(name).row();

        Label heroLabel = WidgetFactory.createLabel("Hero: ");
        createWindow.add(heroLabel).row();

        SelectBox<Hero> heroSelectBox = WidgetFactory.createSelectBox();
        Array<Hero> heros = new Array<>();
        Hero.getHeroes().forEach(heros::add);
        heroSelectBox.setItems(heros);
        createWindow.add(heroSelectBox).row();


        TextButton registerButton = WidgetFactory.createTextButton("Create");
        registerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // send request to create user
                //clientSystem.send(new UserCreateRequest(name.getText(), heroSelectBox.getSelected().ordinal()));
                registerButton.setDisabled(true);
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        registerButton.setDisabled(false);
                    }
                }, 2);
            }
        });
        createWindow.add(registerButton).row();

        TextButton goBackButton = WidgetFactory.createTextButton("Go Back");
        goBackButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                screenManager.to(ScreenEnum.LOGIN);
            }
        });
        createWindow.add(goBackButton).row();

        getMainTable().add(createWindow);
    }
}
