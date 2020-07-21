package game.systems.screen;

import com.artemis.E;
import com.artemis.annotations.Wire;
import com.esotericsoftware.minlog.Log;
import component.position.WorldPos;
import game.systems.PlayerSystem;
import game.systems.actions.PlayerActionSystem;
import game.systems.network.ClientSystem;
import game.systems.resources.MapSystem;
import game.systems.resources.MessageSystem;
import game.systems.resources.ObjectSystem;
import game.systems.ui.UserInterfaceSystem;
import game.systems.ui.action_bar.systems.SpellSystem;
import game.systems.ui.console.ConsoleSystem;
import game.utils.CursorSystem;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import shared.model.Spell;
import shared.model.map.Tile;
import shared.network.interaction.NpcInteractionRequest;
import shared.systems.IntervalSystem;
import shared.util.Messages;

@Wire
public class MouseSystem extends PassiveSystem {

    private CursorSystem cursorSystem;
    private ClientSystem clientSystem;
    private PlayerSystem playerSystem;
    private ConsoleSystem consoleSystem;
    private MapSystem mapSystem;
    private ObjectSystem objectSystem;
    private MessageSystem messageSystem;
    private SpellSystem spellSystem;
    private IntervalSystem intervalSystem;
    private PlayerActionSystem playerActionSystem;

    private UserInterfaceSystem userInterfaceSystem;


    private MouseActionContext action;
    private MouseActionContext defaultAction;
    private WorldPos oldWorldPos = new WorldPos();
    private int tapCounter;

    @Override
    protected void initialize() {

        defaultAction = new MouseActionContext(CursorSystem.AOCursor.HAND, (worldPos -> {
            boolean seeNothing = true;
            if (worldPos.equals(oldWorldPos)) {
                tapCounter++;
            } else {
                oldWorldPos = worldPos;
                tapCounter = 0;
            }
            for (E entity : E.withComponent(WorldPos.class)) {
                if (entity.getWorldPos().equals(worldPos)) {
                    if (entity.hasObject()) {
                        objectSystem.getObject(entity.getObject().index).ifPresent(obj -> {
                            // see object
                            consoleSystem.getConsole().addInfo(messageSystem.getMessage(
                                    Messages.SEE_SOMEONE, String.valueOf(entity.objectCount()))
                                    + " " + obj.getName());
                        });
                    } else if (entity.hasName()) {
                        consoleSystem.getConsole().addInfo(messageSystem.getMessage(Messages.SEE_SOMEONE,
                                entity.getName().text));
                        if (entity.hasNPC() && !entity.isHostile()) {
                            if (tapCounter > 1) {
                                Log.info(" id" + entity.nPCId() + " " + entity.getName().text);
                                clientSystem.send(new NpcInteractionRequest(entity.nPCId()));
                                tapCounter = 0;
                            }
                        }
                    }
                    seeNothing = false;
                    //return;
                }
            }

            Tile targetTile = mapSystem.getTile(worldPos);
            if (targetTile == null) return;

            if (targetTile.getObjIndex() > 0) {
                objectSystem.getObject(targetTile.getObjIndex()).ifPresent(obj -> {
                    consoleSystem.getConsole().addInfo(messageSystem.getMessage(Messages.SEE_SOMEONE, String.valueOf(targetTile.getObjCount()))
                            + " " + obj.getName());
                });
                seeNothing = false;
            }
            if (seeNothing) {
                consoleSystem.getConsole().addInfo(messageSystem.getMessage(Messages.SEE_NOTHING));
            }
        }));
        action = defaultAction;
    }

    private void setAction(MouseActionContext mouseActionContext) {
        action = mouseActionContext;
        cursorSystem.setCursor(action.getCursor());
    }

    private void resetAction() {
        cursorSystem.setCursor(CursorSystem.AOCursor.HAND);
        action = defaultAction;
    }

    public void onClick() {
        if (action == null) {
            Log.debug("BAD: see Mouse System");
            return;
        }
        WorldPos mouseWorldPos = userInterfaceSystem.getMouseWorldPos();
        action.run(mouseWorldPos);
        resetAction();
    }

    public void spell(Spell spell) {
        if (spell != null) {
            setAction(new MouseActionContext(CursorSystem.AOCursor.SELECT, (pos) -> {
                playerActionSystem.castSpell(spell, pos);

            }));
        }
    }

    public void shot() {
        setAction(new MouseActionContext(CursorSystem.AOCursor.SELECT, (pos) -> {
            playerActionSystem.rangedAttack(pos);
        }));
    }

}
