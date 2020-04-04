package game.systems.screen;

import com.artemis.E;
import com.artemis.annotations.Wire;
import com.esotericsoftware.minlog.Log;
import game.systems.PlayerSystem;
import game.systems.network.ClientSystem;
import game.systems.network.TimeSync;
import game.systems.resources.MapSystem;
import game.systems.resources.MessageSystem;
import game.systems.resources.ObjectSystem;
import game.systems.ui.UserInterfaceSystem;
import game.systems.ui.console.ConsoleSystem;
import game.utils.CursorSystem;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import component.position.WorldPos;
import shared.model.Spell;
import shared.network.combat.SpellCastRequest;
import shared.util.Messages;

@Wire
public class MouseSystem extends PassiveSystem {

    private TimeSync timeSyncSystem;
    private CursorSystem cursorSystem;
    private ClientSystem clientSystem;
    private PlayerSystem playerSystem;
    private ConsoleSystem consoleSystem;
    private MapSystem mapSystem;
    private ObjectSystem objectSystem;
    private MessageSystem messageSystem;

    private UserInterfaceSystem userInterfaceSystem;


    private MouseActionContext action;
    private MouseActionContext defaultAction;

    @Override
    protected void initialize() {
        defaultAction = new MouseActionContext(CursorSystem.AOCursor.HAND, (worldPos -> {
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
                    }
                    return;
                }
            }
            consoleSystem.getConsole().addInfo(messageSystem.getMessage(Messages.SEE_NOTHING));
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
            Log.info("BAD: see Mouse System");
            return;
        }
        WorldPos mouseWorldPos = userInterfaceSystem.getMouseWorldPos();
        action.run(mouseWorldPos);
        resetAction();
    }

    public void spell(Spell spell) {
        if (spell != null) {
            setAction(new MouseActionContext(CursorSystem.AOCursor.SELECT, (pos) -> {
                E player = playerSystem.get();
                if (!player.hasAttack() || player.getAttack().interval - world.getDelta() < 0) {
                    long rtt = timeSyncSystem.getRtt();
                    long timeOffset = timeSyncSystem.getTimeOffset();
                    clientSystem.send(new SpellCastRequest(spell, pos, rtt + timeOffset));
                }
            }));
        }
    }

}
