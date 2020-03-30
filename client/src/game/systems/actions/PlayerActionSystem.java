package game.systems.actions;

import com.artemis.annotations.Wire;
import game.systems.PlayerSystem;
import game.systems.network.ClientSystem;
import game.systems.ui.action_bar.systems.InventorySystem;
import game.systems.ui.console.ConsoleSystem;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import shared.model.AttackType;
import shared.network.combat.AttackRequest;
import shared.network.interaction.MeditateRequest;

@Wire
public class PlayerActionSystem extends PassiveSystem {

    private ClientSystem clientSystem;
    private PlayerSystem playerSystem;
    private ConsoleSystem consoleSystem;

    private InventorySystem inventorySystem;

    public void meditate() {
        clientSystem.send(new MeditateRequest());
    }

    public void attack() {
        if (canAttack()) {
            clientSystem.send(new AttackRequest(AttackType.PHYSICAL));
            playerSystem.get().attack();
        } else {
            consoleSystem.addInfo(""); // TODO complete
        }
    }

    public boolean canAttack() {
        // TODO
        return true;
    }


}
