package game.screens;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import game.systems.network.ClientSystem;
import shared.interfaces.Hero;
import shared.network.user.UserCreateRequest;

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
        Window createWindow = new Window("", getSkin());

        Label nameLabel = new Label("Name:", getSkin());
        createWindow.add(nameLabel).row();

        TextField name = new TextField("", getSkin());
        createWindow.add(name).row();

        Label heroLabel = new Label("Hero: ", getSkin());
        createWindow.add(heroLabel).row();

        SelectBox<Hero> heroSelectBox = new SelectBox<>(getSkin());
        Array<Hero> heros = new Array<>();
        Hero.getHeroes().forEach(heros::add);
        heroSelectBox.setItems(heros);
        createWindow.add(heroSelectBox).row();


        TextButton registerButton = new TextButton("Create", getSkin());
        registerButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                // send request to create user
                clientSystem.send(new UserCreateRequest(name.getText(), heroSelectBox.getSelected().ordinal()));
                registerButton.setDisabled(true);
            }
        });
        createWindow.add(registerButton).row();

        TextButton goBackButton = new TextButton("Go Back", getSkin());
        goBackButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                screenManager.to(ScreenEnum.LOGIN);
                registerButton.setDisabled(false);
            }
        });
        createWindow.add(goBackButton).row();

        getMainTable().add(createWindow);
    }
}
