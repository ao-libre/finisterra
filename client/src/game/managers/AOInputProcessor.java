package game.managers;

import com.artemis.E;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.esotericsoftware.minlog.Log;
import game.AOGame;
import game.handlers.AOAssetManager;
import game.handlers.MapHandler;
import game.handlers.MusicHandler;
import game.handlers.ObjectHandler;
import game.screens.GameScreen;
import game.systems.WorkSystem;
import game.screens.transitions.FadingGame;
import game.systems.camera.CameraSystem;
import game.systems.network.TimeSync;
import game.ui.GUI;
import game.utils.AOKeys;
import game.utils.AlternativeKeys;
import game.utils.Cursors;
import game.utils.WorldUtils;
import shared.model.AttackType;
import shared.model.Spell;
import shared.model.map.Map;
import shared.model.map.Tile;
import shared.network.combat.AttackRequest;
import shared.network.combat.SpellCastRequest;
import shared.network.interaction.DropItem;
import shared.network.interaction.MeditateRequest;
import shared.network.interaction.TakeItemRequest;
import shared.network.interaction.TalkRequest;
import shared.network.inventory.ItemActionRequest;
import shared.objects.types.Obj;
import shared.util.MapHelper;
import shared.util.Messages;

import java.util.Optional;

import static com.artemis.E.E;

public class AOInputProcessor extends Stage {

    public static boolean alternativeKeys = false;

    private final GUI gui;
    private final AOAssetManager assetManager;

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
        switch (button) {
            case 0:
                WorldUtils.getWorld().ifPresent(world -> WorldUtils.mouseToWorldPos().ifPresent(worldPos -> {
                    final Optional<Spell> toCast = gui.getSpellView().toCast;
                    final boolean toShoot = gui.getInventory().toShoot;
                    if (toCast.isPresent() || toShoot) {
                        E player = E.E(GameScreen.getPlayer());
                        if (!player.hasAttack() || player.getAttack().interval - world.getDelta() < 0) {
                            TimeSync timeSyncSystem = world.getSystem(TimeSync.class);
                            long rtt = timeSyncSystem.getRtt();
                            long timeOffset = timeSyncSystem.getTimeOffset();
                            if (toShoot) {
                                GameScreen.getClient().sendToAll(new AttackRequest(AttackType.RANGED, worldPos, rtt + timeOffset));
                            } else {
                                Spell spell = toCast.get();
                                GameScreen.getClient().sendToAll(new SpellCastRequest(spell, worldPos, rtt + timeOffset));
                            }
                            player.attack();
                        } else {
                            if (toShoot) {
                                gui.getConsole().addWarning(assetManager.getMessages(Messages.CANT_SHOOT_THAT_FAST));
                            } else {
                                gui.getConsole().addWarning(assetManager.getMessages(Messages.CANT_ATTACK));
                            }
                        }
                        Cursors.setCursor("hand");
                        gui.getSpellView().cleanCast();
                        gui.getInventory().cleanShoot();
                    } else {
                        WorldManager worldManager = world.getSystem(WorldManager.class);
                        Map map = MapHandler.get(worldPos.getMap());
                        Tile tile = MapHelper.getTile(map, worldPos);
                        ObjectHandler objectHandler = WorldUtils.getWorld().orElse(null)
                                .getSystem(ObjectHandler.class);
                        Optional<E> targetEntity = worldManager.getEntities()
                                .stream()
                                .filter(entity -> E(entity).hasWorldPos() && E(entity).getWorldPos().equals(worldPos))
                                .map(E::E)
                                .findFirst();
                        if (targetEntity.isPresent()) {
                            E entity = targetEntity.get();
                            if (entity.hasObject()) {
                                Obj obj = objectHandler.getObject(entity.getObject().index).get();
                                gui.getConsole().addInfo(assetManager.getMessages(
                                        Messages.SEE_SOMEONE, String.valueOf(entity.objectCount()))
                                        + " " + obj.getName());
                            } else if (entity.hasName()) {
                                gui.getConsole().addInfo(assetManager.getMessages(Messages.SEE_SOMEONE,
                                        entity.getName().text));
                            }
                        } else if (tile.getObjIndex() > 0) {
                            objectHandler.getObject(tile.getObjIndex()).ifPresent(obj -> {
                                gui.getConsole().addInfo(assetManager.getMessages(
                                        Messages.SEE_SOMEONE, String.valueOf(tile.getObjCount()))
                                        + " " + obj.getName());
                            });
                        } else {
                            gui.getConsole().addInfo(assetManager.getMessages(Messages.SEE_NOTHING));
                        }
                    }
                }));
                break;
            case 1:
                shoot();
                break;
            case 2: // para implementar mas adelate o hacer test boton del medio
                Log.info("********boton medio******");
                break;
        }
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
            case Input.Keys.NUM_1:
                useActionBarSlot(0);
                break;
            case Input.Keys.NUM_2:
                useActionBarSlot(1);
                break;
            case Input.Keys.NUM_3:
                useActionBarSlot(2);
                break;
            case Input.Keys.NUM_4:
                useActionBarSlot(3);
                break;
            case Input.Keys.NUM_5:
                useActionBarSlot(4);
                break;
            case Input.Keys.NUM_6:
                useActionBarSlot(5);
                break;
            case Input.Keys.NUM_7:
                musicControl(7);
                break;
            case Input.Keys.NUM_8:
                musicControl(8);
                break;
            case Input.Keys.NUM_9:
                musicControl(9);
                break;
            case Input.Keys.Q:
                work();
                break;
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
            case Input.Keys.NUM_1:
                useActionBarSlot(0);
                break;
            case Input.Keys.NUM_2:
                useActionBarSlot(1);
                break;
            case Input.Keys.NUM_3:
                useActionBarSlot(2);
                break;
            case Input.Keys.NUM_4:
                useActionBarSlot(3);
                break;
            case Input.Keys.NUM_5:
                useActionBarSlot(4);
                break;
            case Input.Keys.NUM_6:
                useActionBarSlot(5);
                break;
            case Input.Keys.NUM_7:
                musicControl(7);//play / stop
                break;
            case Input.Keys.NUM_8:
                musicControl(8);//bajar volumen
                break;
            case Input.Keys.NUM_9:
                musicControl(9);//subir volumen
                break;
            case Input.Keys.Q:
                work();
                break;

        }
    }

    private  void work(){
        WorkSystem workSystem = new WorkSystem( gui ) ;
    }

    private void musicControl(int number){

        Music backGroundMusic = MusicHandler.BACKGROUNDMUSIC;
        float volum;
        switch (number) {
            case 7:
                if (!backGroundMusic.isPlaying()) {
                    backGroundMusic.play();
                } else {
                    backGroundMusic.stop();
                }
                break;
            case 8:
                volum = backGroundMusic.getVolume() - 0.01f;
                backGroundMusic.setVolume(volum);
                break;
            case 9:
                volum = backGroundMusic.getVolume() + 0.01f;
                backGroundMusic.setVolume(volum);
                break;
        }
    }

    private void useActionBarSlot(int x) {
        int base;
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
            if (gui.getActionBar().getState().equals("INVENTORY")) {
                if (!gui.getInventory().getSelected().isPresent()) {
                    base = 0;
                } else {
                    base = gui.getInventory().selectedIndex();
                }
                gui.getInventoryQuickBar().addItemsIQB(base, x);
            }
            if (gui.getActionBar().getState().equals("SPELL")) {
                gui.getSpellView().addSpelltoSpellview(gui.getSpellViewExpanded().getSelected(), x);
            }
        } else {
            GameScreen.getClient().sendToAll(new ItemActionRequest(gui.getInventoryQuickBar().getGBases(x)));
            gui.getInventory().cleanShoot();
            Cursors.setCursor("hand");
        }
    }

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

    private void shoot() {
        gui.getInventory().getShoot();
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
            gui.getInventory().isBowORArrow(gui.getInventory().getSelected().get());
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
        gui.getInventoryQuickBar().setVisible(!gui.getInventoryQuickBar().isVisible());
        gui.getActionBar().setExpandButtonVisible();
    }

    private void toggleSpells() {
        gui.getSpellView().setVisible(!gui.getSpellView().isVisible());
        gui.getActionBar().setExpandButtonVisible();
    }

}
