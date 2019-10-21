package game.managers;

import com.artemis.E;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;
import game.AOGame;
import game.handlers.AOAssetManager;
import game.screens.GameScreen;
import game.systems.camera.CameraSystem;
import game.systems.network.TimeSync;
import game.ui.GUI;
import game.utils.AOKeys;
import game.utils.AlternativeKeys;
import game.utils.Cursors;
import game.utils.WorldUtils;
import shared.model.AttackType;
import shared.model.Spell;
import shared.network.combat.AttackRequest;
import shared.network.combat.SpellCastRequest;
import shared.network.interaction.DropItem;
import shared.network.interaction.MeditateRequest;
import shared.network.interaction.TakeItemRequest;
import shared.network.interaction.TalkRequest;
import shared.network.inventory.ItemActionRequest;
import shared.util.Messages;

import java.util.Optional;

import static com.artemis.E.E;

public class AOInputProcessor extends Stage {

    public static boolean alternativeKeys = false;

    private GUI gui;
    private AOAssetManager assetManager;
    //asd
    private int x;
    private int base;
    //asdf
    public AOInputProcessor(GUI gui) {
        this.gui = gui;
        this.assetManager = AOGame.getGlobalAssetManager();
    }

    @Override
    public boolean scrolled(int amount) {
        if (gui.getActionBar().isOver()) {
            gui.getActionBar().scrolled(amount);
        } else {
            WorldUtils.getWorld().ifPresent(world -> {
                CameraSystem system = world.getSystem(CameraSystem.class);
                system.zoom(amount, CameraSystem.ZOOM_TIME);
            });
        }
        return super.scrolled(amount);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        boolean result = super.touchUp(screenX, screenY, pointer, button);
        if (gui.getActionBar().isOver()) {
            return result;
        }

        WorldUtils.getWorld().ifPresent(world -> WorldUtils.mouseToWorldPos().ifPresent(worldPos -> {
            final Optional<Spell> toCast = gui.getSpellView().toCast;
            if (toCast.isPresent()) {
                Spell spell = toCast.get();
                E player = E.E(GameScreen.getPlayer());
                if (!player.hasAttack() || player.getAttack().interval - world.getDelta() < 0) {
                    TimeSync timeSyncSystem = world.getSystem(TimeSync.class);
                    long rtt = timeSyncSystem.getRtt();
                    long timeOffset = timeSyncSystem.getTimeOffset();
                    GameScreen.getClient().sendToAll(new SpellCastRequest(spell, worldPos, rtt + timeOffset));
                    player.attack();
                } else {
                    gui.getConsole().addWarning(assetManager.getMessages(Messages.CANT_ATTACK));
                }
                Cursors.setCursor("hand");
                gui.getSpellView().cleanCast();
            } else {
                WorldManager worldManager = world.getSystem(WorldManager.class);
                Optional<String> name = worldManager.getEntities()
                        .stream()
                        .filter(entity -> E(entity).hasWorldPos() && E(entity).getWorldPos().equals(worldPos))
                        .filter(entity -> E(entity).hasName())
                        .map(entity -> E(entity).getName().text)
                        .findFirst();
                if (name.isPresent()) {
                    gui.getConsole().addInfo(assetManager.getMessages(Messages.SEE_SOMEONE, name.get()));
                } else {
                    gui.getConsole().addInfo(assetManager.getMessages(Messages.SEE_NOTHING));
                }

            }
        }));
        return result;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (!gui.getDialog().isVisible()) {
            if (alternativeKeys) {
                doAlternativeActions(keycode);
            } else {
                doActions(keycode);
            }
        }
        switch (keycode) {
            case AlternativeKeys.TALK:
                toggleDialogText();
                break;
            case Input.Keys.F1:
                alternativeKeys = !alternativeKeys;
                break;
        }

        return super.keyUp(keycode);
    }


    private void doActions(int keycode) {
        switch (keycode) {
            case AOKeys.INVENTORY:
                toggleInventory();
                break;
            case AOKeys.SPELLS:
                toggleSpells();
                break;
            case AOKeys.MEDITATE:
                toggleMeditate();
                break;
            case AOKeys.DROP:
                dropItem();
                break;
            case AOKeys.TAKE:
                takeItem();
                break;
            case AOKeys.EQUIP:
                equip();
                break;
            case AOKeys.USE:
                use();
                break;
            case AOKeys.ATTACK_1:
                attack();
                break;
            case AOKeys.ATTACK_2:
                attack();
                break;
            case Input.Keys.L:
                gui.getActionBar().toggle();
                break;
            case Input.Keys.ESCAPE:
                // Disconnect & go back to LoginScreen
                AOGame game = (AOGame) Gdx.app.getApplicationListener();
                game.toLogin();
                break;
            case Input.Keys.F2:
                // Take a screenshot of the render.
                gui.takeScreenshot();
                break;
            case Input.Keys.F11:
                // Toggle between Windowed Mode and Fullscreen.
                gui.toggleFullscreen();
                break;
            //asd
            case Input.Keys.NUM_1:
                useq1();
                break;
            case Input.Keys.NUM_2:
                useq2();
                break;
            case Input.Keys.NUM_3:
                useq3();
                break;
            case Input.Keys.NUM_4:
                useq4();
                break;
            case Input.Keys.NUM_5:
                useq5();
                break;
            case Input.Keys.NUM_6:
                useq6();
                break;
            case Input.Keys.Q:
                asigQI ();
                break;

//asdf
        }
    }

    private void doAlternativeActions(int keycode) {
        switch (keycode) {
            case AlternativeKeys.INVENTORY:
                toggleInventory();
                break;
            case AlternativeKeys.SPELLS:
                toggleSpells();
                break;
            case AlternativeKeys.MEDITATE:
                toggleMeditate();
                break;
            case AlternativeKeys.DROP:
                dropItem();
                break;
            case AlternativeKeys.TAKE:
                takeItem();
                break;
            case AlternativeKeys.EQUIP:
                equip();
                break;
            case AlternativeKeys.USE:
                use();
                break;
            case AlternativeKeys.ATTACK_1:
                attack();
                break;
            case AlternativeKeys.ATTACK_2:
                attack();
                break;
            case Input.Keys.ESCAPE:
                // Disconnect & go back to LoginScreen
                AOGame game = (AOGame) Gdx.app.getApplicationListener();
                game.toLogin();
                break;
            case Input.Keys.F2:
                // Take a screenshot of the render.
                gui.takeScreenshot();
                break;
            case Input.Keys.F11:
                // Toggle between Windowed Mode and Fullscreen.
                gui.toggleFullscreen();
                break;
            //asd
            case Input.Keys.NUM_1:
                useq1();
                break;
            case Input.Keys.NUM_2:
                useq2();
                break;
            case Input.Keys.NUM_3:
                useq3();
                break;
            case Input.Keys.NUM_4:
                useq4();
                break;
            case Input.Keys.NUM_5:
                useq5();
                break;
            case Input.Keys.NUM_6:
                useq6();
                break;
            case Input.Keys.Q:
                asigQI ();
                break;

//asdf
        }
    }
//asd
    private void useq1() {
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
            x = 0;
            if (!gui.getInventory ( ).getSelected ( ).isPresent ( )){
                base = 0;
                gui.getConsole().addInfo(assetManager.getMessages(Messages.ATTACK_FAILED));
            } else {
                base = gui.getInventory ( ).selectedIndex ( );
            }
            gui.getQuickInventory().agregarCosas(base, x);
        }else {
            GameScreen.getClient().sendToAll(new ItemActionRequest(gui.getQuickInventory().getGBases(0)));
        }

    }

    private void useq2() {
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
            x = 1;
            if (gui.getInventory ( ).getSelected ( ).isEmpty ( )){
                base = 0;
                System.out.println ( "no se seleciono" );
            } else {
                base = gui.getInventory ( ).selectedIndex ( );
            }
            gui.getQuickInventory().agregarCosas(base, x);

        }else {
            GameScreen.getClient().sendToAll(new ItemActionRequest(gui.getQuickInventory().getGBases(1)));
        }
    }

    private void useq3() {
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
            x = 2;
            if (gui.getInventory ( ).getSelected ( ).isEmpty ( )){
                base = 0;
            } else {
                base = gui.getInventory ( ).selectedIndex ( );
            }
            gui.getQuickInventory().agregarCosas(base, x);
        }else {
            GameScreen.getClient().sendToAll(new ItemActionRequest(gui.getQuickInventory().getGBases(2)));
        }
    }

    private void useq4() {
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
            x = 3;
            if (gui.getInventory ( ).getSelected ( ).isEmpty ( )){
                base = 0;
            } else {
                base = gui.getInventory ( ).selectedIndex ( );
            }
            gui.getQuickInventory().agregarCosas(base, x);

        }else {
            GameScreen.getClient().sendToAll(new ItemActionRequest(gui.getQuickInventory().getGBases(3)));
        }
    }

    private void useq5() {
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
            x = 4;
            if (gui.getInventory ( ).getSelected ( ).isEmpty ( )){
                base = 0;
            } else {
                base = gui.getInventory ( ).selectedIndex ( );
            }
            gui.getQuickInventory().agregarCosas(base, x);

        }else {
            GameScreen.getClient().sendToAll(new ItemActionRequest(gui.getQuickInventory().getGBases(4)));
        }
    }

    private void useq6() {
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
            x = 5;
            if (gui.getInventory ( ).getSelected ( ).isEmpty ( )){
                base = 0;
            } else {
                base = gui.getInventory ( ).selectedIndex ( );
            }
            gui.getQuickInventory().agregarCosas(base, x);
        }else {
            GameScreen.getClient().sendToAll(new ItemActionRequest(gui.getQuickInventory().getGBases(5)));
        }
    }

    private void asigQI() {
    /*
        int x = 0;

        int base = gui.getInventory().selectedIndex();

        if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)){
            x = 0;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)){
            x = 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)){
            x = 2;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_4)){
            x = 3;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_5)){
            x = 4;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_6)){
            x = 5;
        }
        if (base >= 0 && base <= 20 ){
            gui.getQuickInventory().agregarCosas(base, x);
        }
     */
        gui.getQuickInventory().setVisible(!gui.getQuickInventory().isVisible());

    }

    // fin asd

    private void use() {
        gui.getInventory()
                .getSelected()
                .ifPresent(slot -> GameScreen.getClient().sendToAll(new ItemActionRequest(gui.getInventory().selectedIndex())));
    }

    private void attack() {
        E player = E(GameScreen.getPlayer());
        WorldUtils.getWorld().ifPresent(world -> {
            if (!player.hasAttack() || player.getAttack().interval - world.getDelta() <= 0) {
                GameScreen.getClient().sendToAll(new AttackRequest(AttackType.PHYSICAL));
                player.attack();
            }
        });
    }

    private void equip() {
        gui.getInventory()
                .getSelected()
                .ifPresent(slot -> GameScreen.getClient().sendToAll(new ItemActionRequest(gui.getInventory().selectedIndex())));
    }

    private void takeItem() {
        GameScreen.getClient().sendToAll(new TakeItemRequest());
    }

    // drop selected item (count 1 for the time being)
    private void dropItem() {
        gui.getInventory().getSelected().ifPresent(selected -> {
            int player = GameScreen.getPlayer();
            GameScreen
                    .getClient()
                    .sendToAll(new DropItem(E(player).getNetwork().id, gui.getInventory().selectedIndex(), E(player).getWorldPos()));
        });
    }

    private void toggleDialogText() {
        if (gui.getDialog().isVisible()) {
            String message = gui.getDialog().getMessage();
            GameScreen.getClient().sendToAll(new TalkRequest(message));
        }
        gui.getDialog().toggle();
        E(GameScreen.getPlayer()).writing(gui.getDialog().isVisible());
    }

    private void toggleMeditate() {
        GameScreen.getClient().sendToAll(new MeditateRequest());
    }

    private void toggleInventory() {
        gui.getInventory().setVisible(!gui.getInventory().isVisible());
    }

    private void toggleSpells() {
        gui.getSpellView().setVisible(!gui.getSpellView().isVisible());
    }

}
