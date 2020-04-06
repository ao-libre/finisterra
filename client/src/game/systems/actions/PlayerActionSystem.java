package game.systems.actions;

import com.artemis.E;
import com.artemis.annotations.Wire;
import component.position.WorldPos;
import game.systems.PlayerSystem;
import game.systems.network.ClientSystem;
import game.systems.network.TimeSync;
import game.systems.resources.MessageSystem;
import game.systems.ui.action_bar.systems.InventorySystem;
import game.systems.ui.action_bar.systems.SpellSystem;
import game.systems.ui.console.ConsoleSystem;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import shared.interfaces.Intervals;
import shared.model.AttackType;
import shared.model.Spell;
import shared.network.combat.AttackRequest;
import shared.network.combat.SpellCastRequest;
import shared.network.interaction.MeditateRequest;
import shared.network.inventory.ItemActionRequest;
import shared.util.Messages;

@Wire
public class PlayerActionSystem extends PassiveSystem {

    private ClientSystem clientSystem;
    private PlayerSystem playerSystem;
    private ConsoleSystem consoleSystem;
    private IntervalSystem intervalSystem;
    private TimeSync timeSyncSystem;

    private SpellSystem spellSystem;
    private MessageSystem messageSystem;
    private InventorySystem inventorySystem;

    public void meditate() {
        clientSystem.send(new MeditateRequest());
    }

    public void attack() {
        if (intervalSystem.canPhysicAttack()) {
            clientSystem.send(new AttackRequest(AttackType.PHYSICAL));
            playerSystem.get().attackIntervalValue(Intervals.ATTACK_INTERVAL);
        } else {
            consoleSystem.getConsole().addWarning(messageSystem.getMessage(Messages.CANT_ATTACK_THAT_FAST));
        }
    }

    public void useItem(int selectedIndex) {
        if (intervalSystem.canUse()) {
            clientSystem.send(new ItemActionRequest(selectedIndex));
            playerSystem.get().useIntervalValue(Intervals.USE_INTERVAL);
        } else {
            consoleSystem.getConsole().addWarning(messageSystem.getMessage(Messages.CANT_USE_THAT_FAST));
        }
    }

    public void castSpell(Spell spell, WorldPos pos) {
        E player = playerSystem.get();
        if (intervalSystem.canSpellAttack()) {
            long rtt = timeSyncSystem.getRtt();
            long timeOffset = timeSyncSystem.getTimeOffset();
            clientSystem.send(new SpellCastRequest(spell, pos, rtt + timeOffset));
            spellSystem.clearCast();
            player.attackIntervalValue(Intervals.MAGIC_ATTACK_INTERVAL);
        } else {
            consoleSystem.getConsole().addWarning(messageSystem.getMessage(Messages.CANT_MAGIC_THAT_FAST));
        }
    }
}
