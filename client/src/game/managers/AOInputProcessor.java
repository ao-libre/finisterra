package game.managers;

import com.artemis.E;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.esotericsoftware.minlog.Log;
import game.AOGame;
import game.handlers.DescriptorHandler;
import game.screens.GameScreen;
import game.screens.LoginScreen;
import game.ui.GUI;
import game.utils.AlternativeKeys;
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

import java.util.Optional;
import java.util.Random;

import static com.artemis.E.E;

public class AOInputProcessor extends Stage {

    private static final Random r = new Random();

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        boolean result = super.touchUp(screenX, screenY, pointer, button);
        if (GUI.getSpellView().isOver() || GUI.getInventory().isOver()) {
            return result;
        }
        WorldUtils.mouseToWorldPos().ifPresent(worldPos -> {
            final Optional<Spell> toCast = GUI.getSpellView().toCast;
            if (toCast.isPresent()) {
                Spell spell = toCast.get();
                E player = E.E(GameScreen.getPlayer());
                if (!player.hasAttack() || player.getAttack().interval - GameScreen.getWorld().getDelta() < 0) {
                    GameScreen.getClient().sendToAll(new SpellCastRequest(spell, worldPos));
                    player.attackInterval();
                } else {
                    // TODO can't attack because interval
                }
                Pixmap pm = new Pixmap(Gdx.files.internal("data/ui/images/cursor-arrow.png"));
                Gdx.graphics.setCursor(Gdx.graphics.newCursor(pm, 10, 4));
                pm.dispose();
                GUI.getSpellView().toCast = Optional.empty();
            } else {
                Optional<String> name = WorldManager.getEntities()
                        .stream()
                        .filter(entity -> E(entity).hasWorldPos() && E(entity).getWorldPos().equals(worldPos))
                        .filter(entity -> E(entity).hasName())
                        .map(entity -> E(entity).getName().text)
                        .findFirst();
                if (name.isPresent()) {
                    GUI.getConsole().addMessage("Ves a " + name.get());
                } else {
                    GUI.getConsole().addMessage("No ves nada interesante");
                }

            }
        });
        return result;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (!GUI.getDialog().isVisible()) {
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
                case Input.Keys.O: // testing fxs
                    int randomFx = r.nextInt(DescriptorHandler.getFxs().size());
                    Log.info("FX: " + randomFx);
                    E(GameScreen.getPlayer()).fXAddFx(randomFx);
                    break;
                case Input.Keys.ESCAPE:
                    // Disconnect & go back to LoginScreen
                    AOGame game = (AOGame) Gdx.app.getApplicationListener();
                    game.getClientSystem().stop();
                    game.setScreen(new LoginScreen());
            }
        }
        switch (keycode) {
            case AlternativeKeys.TALK:
                toggleDialogText();
                break;
        }

        return super.keyUp(keycode);
    }

    private void use() {
        GUI.getInventory().getSelected().ifPresent(slot -> {
            GameScreen.getClient().sendToAll(new ItemActionRequest(GUI.getInventory().selectedIndex()));
        });
    }

    private void attack() {
        E player = E(GameScreen.getPlayer());
        if (!player.hasAttack() || player.getAttack().interval - GameScreen.getWorld().getDelta() <= 0) {
            GameScreen.getClient().sendToAll(new AttackRequest(AttackType.PHYSICAL));
            player.attackInterval();
        }
    }

    private void equip() {
        GUI.getInventory().getSelected().ifPresent(slot -> {
            GameScreen.getClient().sendToAll(new ItemActionRequest(GUI.getInventory().selectedIndex()));
        });
    }

    private void takeItem() {
        GameScreen.getClient().sendToAll(new TakeItemRequest());
    }

    // drop selected item (count 1 for the time being)
    private void dropItem() {
        GUI.getInventory().getSelected().ifPresent(selected -> {
            int player = GameScreen.getPlayer();
            GameScreen.getClient().sendToAll(new DropItem(E(player).getNetwork().id, GUI.getInventory().selectedIndex(), E(player).getWorldPos()));
        });
    }

    private void toggleDialogText() {
        if (GUI.getDialog().isVisible()) {
            String message = GUI.getDialog().getMessage();
            GameScreen.getClient().sendToAll(new TalkRequest(message));
        }
        GUI.getDialog().toggle();
        E(GameScreen.getPlayer()).writing(GUI.getDialog().isVisible());
    }

    private void toggleMeditate() {
        GameScreen.getClient().sendToAll(new MeditateRequest());
    }

    private void toggleInventory() {
        GUI.getInventory().setVisible(!GUI.getInventory().isVisible());
    }

    private void toggleSpells() {
        GUI.getSpellView().setVisible(!GUI.getSpellView().isVisible());
    }

}
