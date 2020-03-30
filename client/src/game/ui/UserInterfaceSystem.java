package game.ui;

import camera.Focused;
import com.artemis.Aspect;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import entity.character.status.Health;
import entity.character.status.Stamina;
import game.systems.input.InputSystem;
import game.ui.user.UserInformation;
import position.WorldPos;

public class UserInterfaceSystem extends IteratingSystem implements Disposable {

    //public static final int CONSOLE_TOP_BORDER = 16;
    //public static final int CONSOLE_LEFT_BORDER = 5;
    private Stage stage;
    private ActionBar actionBar;
    private UserInformation userTable;
    private DialogText dialog;
    private AOConsole console;

    private InputSystem inputSystem;

    public UserInterfaceSystem() {
        super(Aspect.all(WorldPos.class, Focused.class, Health.class, Stamina.class));
        this.stage = new Stage();
    }

    @Override
    protected void process(int entityId) {
        draw(world.getDelta());
    }

    public ActionBar getActionBar() {
        return actionBar;
    }

//    public Inventory getInventory() {
//        return actionBar.getInventory();
//    }

    public InventoryQuickBar getInventoryQuickBar() {
        return actionBar.getInventoryQuickBar();
    }

    public DialogText getDialog() {
        return dialog;
    }

    public AOConsole getConsole() {
        return console;
    }

    public UserInformation getUserTable() {
        return userTable;
    }

    public Stage getStage() {
        return stage;
    }

    public SpellView getSpellView() {
        return getActionBar().getSpellView();
    }

    public SpellViewExpanded getSpellViewExpanded() {
        return getActionBar().getSpellViewExpanded();
    }


    @Override
    public void initialize() {

    }

    private void createConsole(Table table) {
        console = new AOConsole();
        table.add(console).left().top();
    }

    private void createUserStatus(Table table) {
        userTable = new UserInformation();
        table.add(userTable).prefWidth(400).left().bottom().expandX();
    }

    private void createActionBar(Table table) {
        actionBar = new ActionBar();
        table.add(actionBar).right().expandY().expandX();
    }

    private void createDialogContainer(Table table) {
        dialog = new DialogText();
        float width = getWidth() * 0.8f;
        dialog.setSize(width, dialog.getHeight());
        dialog.setPosition((getWidth() - width) / 2, getHeight() / 2);
        stage.addActor(dialog);
    }

    private float getHeight() {
        return Gdx.graphics.getHeight();
    }

    private float getWidth() {
        return Gdx.graphics.getWidth();
    }

    public void draw(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

}
